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
package org.apache.karaf.bundle.command;

import java.util.List;

import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.karaf.shell.util.ShellUtil;
import org.osgi.framework.Bundle;

public abstract class BundlesCommand extends OsgiCommandSupport {

    @Argument(index = 0, name = "ids", description = "The list of bundle (identified by IDs or name or name/version) separated by whitespaces", required = false, multiValued = true)
    List<String> ids;
    
    boolean defaultAllBundles = true;

    BundleService bundleService;
    
    public BundlesCommand(boolean defaultAllBundles) {
        this.defaultAllBundles = defaultAllBundles;
    }
    
    protected Object doExecute() throws Exception {
        doExecute(true);
        return null;
    }

    protected Object doExecute(boolean force) throws Exception {
        List<Bundle> bundles = bundleService.selectBundles(ids, defaultAllBundles);
        if (!force) {
            assertNoSystemBundles(bundles);
        }
        doExecute(bundles);
        return null;
    }
    
    private void assertNoSystemBundles(List<Bundle> bundles) {
        for (Bundle bundle : bundles) {
            if (ShellUtil.isASystemBundle(bundleContext, bundle)) {
                throw new RuntimeException("Access to system bundle " + bundle.getBundleId() + " denied. You can override with -f");
            }
        }
    }
      
    protected abstract void doExecute(List<Bundle> bundles) throws Exception;

    public void setBundleService(BundleService bundleSelector) {
        this.bundleService = bundleSelector;
    }

}
