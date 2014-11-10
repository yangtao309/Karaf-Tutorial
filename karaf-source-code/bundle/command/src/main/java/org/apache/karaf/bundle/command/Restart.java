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

import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.MultiException;
import org.osgi.framework.Bundle;

@Command(scope = "bundle", name = "restart", description = "Restarts bundles.")
public class Restart extends BundlesCommandWithConfirmation {
    
    public Restart() {
        errorMessage = "Error restarting bundle";
    }

    protected void doExecute(List<Bundle> bundles) throws Exception {
        if (bundles.isEmpty()) {
            System.err.println("No bundles specified.");
            return;
        }
        List<Exception> exceptions = new ArrayList<Exception>();
        for (Bundle bundle : bundles) {
            try {
                bundle.stop();
            } catch (Exception e) {
                exceptions.add(new Exception("Unable to stop bundle " + bundle.getBundleId() + ": " + e.getMessage(), e));
            }
        }
        for (Bundle bundle : bundles) {
            try {
                bundle.start();
            } catch (Exception e) {
                exceptions.add(new Exception("Unable to start bundle " + bundle.getBundleId() + ": " + e.getMessage(), e));
            }
        }
        MultiException.throwIf("Error restarting bundles", exceptions);
    }

    @Override
    protected void executeOnBundle(Bundle bundle) throws Exception {
    }

}
