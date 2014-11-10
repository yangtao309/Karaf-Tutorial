/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.karaf.features.management.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.List;

import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;

import org.apache.karaf.features.*;
import org.apache.karaf.features.management.FeaturesServiceMBean;
import org.apache.karaf.features.management.codec.JmxFeature;
import org.apache.karaf.features.management.codec.JmxFeatureEvent;
import org.apache.karaf.features.management.codec.JmxRepository;
import org.apache.karaf.features.management.codec.JmxRepositoryEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Implementation of {@link FeaturesServiceMBean}.
 */
public class FeaturesServiceMBeanImpl extends StandardEmitterMBean implements
        MBeanRegistration, FeaturesServiceMBean {

    private ServiceRegistration<FeaturesListener> registration;

    private BundleContext bundleContext;

    private ObjectName objectName;

    private volatile long sequenceNumber = 0;

    private org.apache.karaf.features.FeaturesService featuresService;

    public FeaturesServiceMBeanImpl() throws NotCompliantMBeanException {
        super(FeaturesServiceMBean.class);
    }

    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        objectName = name;
        return name;
    }

    public void postRegister(Boolean registrationDone) {
        registration = bundleContext.registerService(FeaturesListener.class,
                getFeaturesListener(), new Hashtable<String, String>());
    }

    public void preDeregister() throws Exception {
        registration.unregister();
    }

    public void postDeregister() {
    }

    /**
     * {@inheritDoc}
     */
    public TabularData getFeatures() throws Exception {
        try {
            List<Feature> allFeatures = Arrays.asList(featuresService.listFeatures());
            List<Feature> insFeatures = Arrays.asList(featuresService.listInstalledFeatures());
            ArrayList<JmxFeature> features = new ArrayList<JmxFeature>();
            for (Feature feature : allFeatures) {
                try {
                    features.add(new JmxFeature(feature, insFeatures.contains(feature)));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            TabularData table = JmxFeature.tableFrom(features);
            return table;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public TabularData getRepositories() throws Exception {
        try {
            List<Repository> allRepositories = Arrays.asList(featuresService.listRepositories());
            ArrayList<JmxRepository> repositories = new ArrayList<JmxRepository>();
            for (Repository repository : allRepositories) {
                try {
                    repositories.add(new JmxRepository(repository));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            TabularData table = JmxRepository.tableFrom(repositories);
            return table;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public void addRepository(String uri) throws Exception {
        featuresService.addRepository(new URI(uri));
    }

    public void addRepository(String uri, boolean install) throws Exception {
        featuresService.addRepository(new URI(uri), install);
    }

    public void removeRepository(String uri) throws Exception {
        featuresService.removeRepository(new URI(uri));
    }

    public void removeRepository(String uri, boolean uninstall) throws Exception {
        featuresService.removeRepository(new URI(uri), uninstall);
    }

    public void installFeature(String name) throws Exception {
        featuresService.installFeature(name);
    }

    public void installFeature(String name, boolean noClean, boolean noRefresh) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noClean) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoCleanIfFailure);
        }
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        featuresService.installFeature(name, options);
    }

    public void installFeature(String name, boolean noClean, boolean noRefresh, boolean noStart) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noClean) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoCleanIfFailure);
        }
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        if (noStart) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoStartBundles);
        }
        featuresService.installFeature(name, options);
    }

    public void installFeature(String name, String version) throws Exception {
        featuresService.installFeature(name, version);
    }

    public void installFeature(String name, String version, boolean noClean, boolean noRefresh) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noClean) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoCleanIfFailure);
        }
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        featuresService.installFeature(name, version, options);
    }

    public void installFeature(String name, String version, boolean noClean, boolean noRefresh, boolean noStart) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noClean) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoCleanIfFailure);
        }
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        if (noStart) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoStartBundles);
        }
        featuresService.installFeature(name, version, options);
    }

    public TabularData infoFeature(String name) throws Exception {
        try {
            Feature feature = featuresService.getFeature(name);
            return infoFeature(feature);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public TabularData infoFeature(String name, String version) throws Exception {
        try {
            Feature feature = featuresService.getFeature(name, version);
            return infoFeature(feature);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private TabularData infoFeature(Feature feature) throws Exception {
        JmxFeature jmxFeature = null;
        if (featuresService.isInstalled(feature)) {
            jmxFeature = new JmxFeature(feature, true);
        } else {
            jmxFeature = new JmxFeature(feature, false);
        }
        ArrayList<JmxFeature> features = new ArrayList<JmxFeature>();
        features.add(jmxFeature);
        TabularData table = JmxFeature.tableFrom(features);
        return table;
    }

    public void uninstallFeature(String name) throws Exception {
        featuresService.uninstallFeature(name);
    }

    public void uninstallFeature(String name, boolean noRefresh) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        featuresService.uninstallFeature(name, options);
    }

    public void uninstallFeature(String name, String version) throws Exception {
        featuresService.uninstallFeature(name, version);
    }

    public void uninstallFeature(String name, String version, boolean noRefresh) throws Exception {
        EnumSet<org.apache.karaf.features.FeaturesService.Option> options = EnumSet.noneOf(org.apache.karaf.features.FeaturesService.Option.class);
        if (noRefresh) {
            options.add(org.apache.karaf.features.FeaturesService.Option.NoAutoRefreshBundles);
        }
        featuresService.uninstallFeature(name, version, options);
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setFeaturesService(org.apache.karaf.features.FeaturesService featuresService) {
        this.featuresService = featuresService;
    }

    public FeaturesListener getFeaturesListener() {
        return new FeaturesListener() {
            public void featureEvent(FeatureEvent event) {
                if (!event.isReplay()) {
                    Notification notification = new Notification(FEATURE_EVENT_TYPE, objectName, sequenceNumber++);
                    notification.setUserData(new JmxFeatureEvent(event).asCompositeData());
                    sendNotification(notification);
                }
            }

            public void repositoryEvent(RepositoryEvent event) {
                if (!event.isReplay()) {
                    Notification notification = new Notification(REPOSITORY_EVENT_TYPE, objectName, sequenceNumber++);
                    notification.setUserData(new JmxRepositoryEvent(event).asCompositeData());
                    sendNotification(notification);
                }
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                return o.equals(this);
            }

        };
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return getBroadcastInfo();
    }

    private static MBeanNotificationInfo[] getBroadcastInfo() {
        String type = Notification.class.getCanonicalName();
        MBeanNotificationInfo info1 = new MBeanNotificationInfo(new String[]{FEATURE_EVENT_EVENT_TYPE},
                type, "Some features notification");
        MBeanNotificationInfo info2 = new MBeanNotificationInfo(new String[]{REPOSITORY_EVENT_EVENT_TYPE},
                type, "Some repository notification");
        return new MBeanNotificationInfo[]{info1, info2};
    }

}
