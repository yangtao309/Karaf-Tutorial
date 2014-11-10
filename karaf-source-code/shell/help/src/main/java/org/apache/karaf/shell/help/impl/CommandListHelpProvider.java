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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import jline.Terminal;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.CommandWithAction;
import org.apache.karaf.shell.commands.basic.AbstractCommand;
import org.apache.karaf.shell.commands.meta.ActionMetaDataFactory;
import org.apache.karaf.shell.console.HelpProvider;
import org.apache.karaf.shell.console.NameScoping;
import org.apache.karaf.shell.console.SessionProperties;
import org.apache.karaf.shell.table.Col;
import org.apache.karaf.shell.table.ShellTable;
import org.fusesource.jansi.Ansi;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class CommandListHelpProvider implements HelpProvider {

    public static final String COMMANDS = ".commands";

    public String getHelp(CommandSession session, String path) {
        if (path.indexOf('|') > 0) {
            if (path.startsWith("command-list|")) {
                path = path.substring("command-list|".length());
            } else {
                return null;
            }
        }
        SortedMap<String, String> commands = getCommandDescriptions(session, path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        printMethodList(session, new PrintStream(baos), commands);
        return baos.toString();
    }

    private SortedMap<String, String> getCommandDescriptions(CommandSession session, String command) {
        Set<String> names = (Set<String>) session.get(COMMANDS);

        String subshell = (String) session.get("SUBSHELL");
        String completionMode = (String) session.get(SessionProperties.COMPLETION_MODE);

        SortedMap<String,String> commands = new TreeMap<String,String>();
        for (String name : names) {

            if (command != null && !name.startsWith(command)) {
                continue;
            }

            if (completionMode != null && completionMode.equalsIgnoreCase("subshell")) {
                // filter the help only for "global" commands
                if (subshell == null || subshell.trim().isEmpty()) {
                    if (!name.startsWith("*")) {
                        continue;
                    }
                }
            }

            if (completionMode != null && (completionMode.equalsIgnoreCase("subshell") || completionMode.equalsIgnoreCase("first"))) {
                // filter the help only for commands local to the subshell
                if (!name.startsWith(subshell)) {
                    continue;
                }
            }

            String description = null;
            Function function = (Function) session.get(name);
            function = unProxy(function);
            if (function instanceof CommandWithAction) {
                try {
                    Class<? extends Action> actionClass = ((CommandWithAction) function).getActionClass();
                    Command ann = new ActionMetaDataFactory().getCommand(actionClass);
                    description = ann.description();
                } catch (Throwable e) {
                }
                if (name.startsWith("*:")) {
                    name = name.substring(2);
                }
                if (subshell != null && !subshell.trim().isEmpty() && name.startsWith(subshell + ":")) {
                    name = name.substring(subshell.length() + 1);
                }
                commands.put(name, description);
            }
        }
        return commands;
    }

    protected void printMethodList(CommandSession session, PrintStream out, SortedMap<String, String> commands) {
        Terminal term = (Terminal) session.get(".jline.terminal");
        int termWidth = term != null ? term.getWidth() : 80;
        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("COMMANDS").a(Ansi.Attribute.RESET));
        ShellTable table = new ShellTable().noHeaders().separator(" ").size(termWidth);
        table.column(new Col("Command").maxSize(64));
        table.column(new Col("Description"));
        for (Map.Entry<String,String> entry : commands.entrySet()) {
            String key = NameScoping.getCommandNameWithoutGlobalPrefix(session, entry.getKey());
            table.addRow().addContent(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(key).a(Ansi.Attribute.RESET), entry.getValue());
        }
        table.print(out, true);
    }
    
    protected Function unProxy(Function function) {
        try {
            if (function.getClass().getName().contains("CommandProxy")) {
                Field contextField = function.getClass().getDeclaredField("context");
                Field referenceField = function.getClass().getDeclaredField("reference");
                contextField.setAccessible(true);
                referenceField.setAccessible(true);
                BundleContext context = (BundleContext) contextField.get(function);
                ServiceReference reference = (ServiceReference) referenceField.get(function);
                Object target = context.getService(reference);
                try {
                    if (target instanceof Function) {
                        function = (Function) target;
                    }
                } finally {
                    context.ungetService(reference);
                }
            }
        } catch (Throwable t) {
        }
        return function;
    }

}
