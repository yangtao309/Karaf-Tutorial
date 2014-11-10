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

import java.io.InputStream;
import java.net.URL;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.osgi.framework.Bundle;

@Command(scope = "bundle", name = "update", description = "Update bundle.")
public class Update extends BundleCommandWithConfirmation {

    @Argument(index = 1, name = "location", description = "The bundles update location", required = false, multiValued = false)
    String location;

    protected void doExecute(Bundle bundle) throws Exception {
        InputStream is = null;
        if (location != null) {
            try {
                is = new URL(location).openStream();
                bundle.update(is);
            } finally {
                is.close();
            }
        } else {
            bundle.update();
        }
    }

}
