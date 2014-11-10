/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.shell.help.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.service.command.CommandSession;
import org.apache.felix.utils.properties.InterpolationHelper;
import org.apache.karaf.shell.console.HelpProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class HelpSystem implements HelpProvider {

    private BundleContext context;

    public HelpSystem(BundleContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<HelpProvider> getProviders() {
        ServiceReference<HelpProvider> [] refs = null;
        try {
            refs = context.getServiceReferences(HelpProvider.class, null).toArray(new ServiceReference[]{});
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
        Arrays.sort(refs);
        List<HelpProvider> providers = new ArrayList<HelpProvider>();
        for (int i = refs.length - 1; i >= 0; i--) {
            providers.add(context.getService(refs[i]));
        }
        return providers;
    }
    
    public String getHelp(final CommandSession session, String path) {
        if (path == null) {
            path = "%root%";
        }
        Map<String,String> props = new HashMap<String,String>();
        props.put("data", "${" + path + "}");
        final List<HelpProvider> providers = getProviders();
        InterpolationHelper.performSubstitution(props, new InterpolationHelper.SubstitutionCallback() {
            public String getValue(final String key) {
                for (HelpProvider hp : providers) {
                    String result = hp.getHelp(session, key);
                    if (result != null) {
                        return removeNewLine(result);
                    }
                }
                return null;
            }
        });
        return props.get("data");
    }
    
    private String removeNewLine(String help) {
        if (help != null && help.endsWith("\n")) {
            help = help.substring(0, help.length()  -1);
        }
        return help;
    }
    
}
