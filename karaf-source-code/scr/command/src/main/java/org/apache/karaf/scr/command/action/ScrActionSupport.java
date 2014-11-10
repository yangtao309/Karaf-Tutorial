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
package org.apache.karaf.scr.command.action;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.scr.command.ScrCommandConstants;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CommandSessionHolder;
import org.apache.karaf.shell.console.SubShellAction;
import org.apache.karaf.shell.console.completer.ArgumentCompleter;
import org.fusesource.jansi.Ansi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public abstract class ScrActionSupport extends SubShellAction {

    @Option(name = ScrActionSupport.SHOW_ALL_OPTION, aliases = {ScrActionSupport.SHOW_ALL_ALIAS}, description = "Show all Components including the System Components (hidden by default)", required = false, multiValued = false)
    boolean showHidden = false;

    public static final String SHOW_ALL_OPTION = "-s";
    public static final String SHOW_ALL_ALIAS = "--show-hidden";
    
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private ScrService scrService;

    public ScrActionSupport() {
        setSubShell("scr");
    }

    @Override
    public Object doExecute() throws Exception {
        if (scrService == null) {
            String msg = "ScrService is unavailable";
            System.out.println(msg);
            logger.warn(msg);
        } else {
            doScrAction(scrService);
        }
        return null;
    }

    protected abstract Object doScrAction(ScrService scrService) throws Exception;

    protected boolean isActionable(Component component) {
        boolean answer = true;
        return answer;
    }

    public static boolean showHiddenComponent(Component component) {
        boolean answer = false;
        // first look to see if the show all option is there
        // if it is we set showAllFlag to true so the next section will be skipped
        CommandSession commandSession = CommandSessionHolder.getSession();
        ArgumentCompleter.ArgumentList list = (ArgumentCompleter.ArgumentList) commandSession.get(ArgumentCompleter.ARGUMENTS_LIST);
        if (list != null && list.getArguments() != null && list.getArguments().length > 0) {
            List<String> arguments = Arrays.asList(list.getArguments());
            if (arguments.contains(ScrActionSupport.SHOW_ALL_OPTION) || arguments.contains(ScrActionSupport.SHOW_ALL_ALIAS)) {
                answer = true;
            }
        }

        return answer;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isHiddenComponent(Component component) {
        boolean answer = false;
        Hashtable properties = (Hashtable) component.getProperties();
        if (properties != null && properties.containsKey(ScrCommandConstants.HIDDEN_COMPONENT_KEY)) {
            String value = (String) properties.get(ScrCommandConstants.HIDDEN_COMPONENT_KEY);
            // if the value is false, show the hidden
            // then someone wants us to display the name of a hidden component
            if (value != null && value.equals("true")) {
                answer = true;
            }
        }
        return answer;
    }

    /**
     * Get the bundleContext Object associated with this instance of
     * ScrActionSupport.
     * 
     * @return the bundleContext
     */
    public BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(ListAction.class).getBundleContext();
    }

    /**
     * Get the scrService Object associated with this instance of
     * ScrActionSupport.
     * 
     * @return the scrService
     */
    public ScrService getScrService() {
        return scrService;
    }

    /**
     * Sets the scrService Object for this ScrActionSupport instance.
     * 
     * @param scrService the scrService to set
     */
    public void setScrService(ScrService scrService) {
        this.scrService = scrService;
    }

}
