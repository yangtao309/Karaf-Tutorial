/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.bundle.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.bundle.core.BundleWatcher;
import org.apache.karaf.util.maven.Parser;
import org.osgi.framework.*;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Runnable singleton which watches at the defined location for bundle
 * updates.
 */
public class BundleWatcherImpl implements Runnable, BundleListener, BundleWatcher {

    private final Logger logger = LoggerFactory.getLogger(BundleWatcherImpl.class);

    private BundleContext bundleContext;
	private final BundleService bundleService;
	private final MavenConfigService localRepoDetector;

    private AtomicBoolean running = new AtomicBoolean(false);
    private long interval = 1000L;
    private List<String> watchURLs = new CopyOnWriteArrayList<String>();
    private AtomicInteger counter = new AtomicInteger(0);

    /**
     * Constructor
     */
    @SuppressWarnings("deprecation")
    public BundleWatcherImpl(BundleContext bundleContext, MavenConfigService mavenConfigService, BundleService bundleService) {
        this.bundleContext = bundleContext;
		this.localRepoDetector = mavenConfigService;
        this.bundleService = bundleService;
    }

    /* (non-Javadoc)
     * @see org.apache.karaf.dev.core.internal.BundleWatcher#bundleChanged(org.osgi.framework.BundleEvent)
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.INSTALLED || event.getType() == BundleEvent.UNINSTALLED) {
            counter.incrementAndGet();
        }
    }

    public void run() {
        logger.debug("Bundle watcher thread started");
        int oldCounter = -1;
        Set<Bundle> watchedBundles = new HashSet<Bundle>();
        while (running.get() && watchURLs.size() > 0) {
            if (oldCounter != counter.get()) {
                oldCounter = counter.get();
                watchedBundles.clear();
                for (String bundleURL : watchURLs) {
                    for (Bundle bundle : bundleService.getBundlesByURL(bundleURL)) {
                        watchedBundles.add(bundle);
                    }
                }
            }
            if (watchedBundles.size() > 0) {
                // Get the wiring before any in case of a refresh of a dependency
                FrameworkWiring wiring = bundleContext.getBundle(0).adapt(FrameworkWiring.class);
                File localRepository = this.localRepoDetector.getLocalRepository();
                List<Bundle> updated = new ArrayList<Bundle>();
                for (Bundle bundle : watchedBundles) {
                    try {
                        updateBundleIfNecessary(localRepository, updated, bundle);
                    } catch (IOException ex) {
                        logger.error("Error watching bundle.", ex);
                    } catch (BundleException ex) {
                        logger.error("Error updating bundle.", ex);
                    }
                }
                try {
                    final CountDownLatch latch = new CountDownLatch(1);
                    wiring.refreshBundles(updated, new FrameworkListener() {
                        public void frameworkEvent(FrameworkEvent event) {
                            latch.countDown();
                        }
                    });
                    latch.await();
                } catch (InterruptedException e) {
                    running.set(false);
                }
                for (Bundle bundle : updated) {
                    try {
                        if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null) {
                            logger.info("[Watch] Bundle {} is a fragment, so it's not started", bundle.getSymbolicName());
                        } else {
                            bundle.start(Bundle.START_TRANSIENT);
                        }
                    } catch (BundleException ex) {
                        logger.warn("[Watch] Error starting bundle", ex);
                    }
                }
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                running.set(false);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Bundle watcher thread stopped");
        }
    }

    private void updateBundleIfNecessary(File localRepository, List<Bundle> updated, Bundle bundle)
        throws FileNotFoundException, BundleException, IOException {
        File location = getBundleExternalLocation(localRepository, bundle);
        if (location != null && location.exists() && location.lastModified() > bundle.getLastModified()) {
            InputStream is = new FileInputStream(location);
            try {
                logger.info("[Watch] Updating watched bundle: {} ({})", bundle.getSymbolicName(), bundle.getVersion());
                if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null) {
                    logger.info("[Watch] Bundle {} is a fragment, so it's not stopped", bundle.getSymbolicName());
                } else {
                    bundle.stop(Bundle.STOP_TRANSIENT);
                }
                bundle.update(is);
                updated.add(bundle);
            } finally {
                is.close();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.karaf.dev.core.internal.BundleWatcher#add(java.lang.String)
     */
    @Override
    public void add(String url) {
        boolean shouldStart = running.get() && (watchURLs.size() == 0);
        if (!watchURLs.contains(url)) {
            watchURLs.add(url);
            counter.incrementAndGet();
        }
        if (shouldStart) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.karaf.dev.core.internal.BundleWatcher#remove(java.lang.String)
     */
    @Override
    public void remove(String url) {
        watchURLs.remove(url);
        counter.incrementAndGet();
    }

    /**
     * Returns the location of the Bundle inside the local maven repository.
     * 
     * @param bundle
     * @return
     */
    private File getBundleExternalLocation(File localRepository, Bundle bundle) {
        try {
            Parser p = new Parser(bundle.getLocation().substring(4));
            return new File(localRepository.getPath() + File.separator + p.getArtifactPath());
        } catch (MalformedURLException e) {
            logger.error("Could not parse artifact path for bundle" + bundle.getSymbolicName(), e);
        }
        return null;
    }

    public void start() {
        bundleContext.addBundleListener(this);
        // start the watch thread
        if (running.compareAndSet(false, true)) {
            if (watchURLs.size() > 0) {
                Thread thread = new Thread(this);
                thread.start();
            }
        }
    }

    /**
     * Stops the execution of the thread and releases the singleton instance
     */
    public void stop() {
        running.set(false);
        bundleContext.removeBundleListener(this);
    }

    /* (non-Javadoc)
     * @see org.apache.karaf.dev.core.internal.BundleWatcher#getWatchURLs()
     */
    @Override
    public List<String> getWatchURLs() {
        return watchURLs;
    }

    /* (non-Javadoc)
     * @see org.apache.karaf.dev.core.internal.BundleWatcher#setWatchURLs(java.util.List)
     */
    @Override
    public void setWatchURLs(List<String> watchURLs) {
        this.watchURLs = watchURLs;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isRunning() {
        return running.get();
    }

	@Override
	public List<Bundle> getBundlesByURL(String urlFilter) {
		return bundleService.getBundlesByURL(urlFilter);
	}

}
