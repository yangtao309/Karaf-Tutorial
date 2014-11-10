/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.shell.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import jline.console.ConsoleReader;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.commands.CommandException;
import org.apache.karaf.shell.console.SessionProperties;
import org.fusesource.jansi.Ansi;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellUtil.class);

    public static String getBundleName(Bundle bundle) {
        if (bundle != null) {
            String name = (String) bundle.getHeaders().get(Constants.BUNDLE_NAME);
            return (name == null)
                    ? "Bundle " + Long.toString(bundle.getBundleId())
                    : name + " (" + Long.toString(bundle.getBundleId()) + ")";
        }
        return "[STALE BUNDLE]";
    }

    public static String getUnderlineString(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append('-');
        }
        return sb.toString();
    }

    public static String getValueString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof Collection<?>) {
            Object[] array = ((Collection<?>) obj).toArray();
            return getValueString(array);
        } else if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < array.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(getValueString(array[i]));
            }
            sb.append("]");
            return sb.toString();
        } else {
            return obj.toString();
        }
    }

    /**
     * Check if a bundle is a system bundle (start level < 50)
     *
     * @param bundleContext
     * @param bundle
     * @return true if the bundle has start level minor than 50
     */
    public static boolean isASystemBundle(BundleContext bundleContext, Bundle bundle) {
        int level = bundle.adapt(BundleStartLevel.class).getStartLevel();
        int sbsl = 49;
        final String sbslProp = bundleContext.getProperty("karaf.systemBundlesStartLevel");
        if (sbslProp != null) {
            try {
                sbsl = Integer.valueOf(sbslProp);
            } catch (Exception ignore) {
                // ignore
            }
        }
        return level <= sbsl;
    }

    /**
     * Ask the user to confirm the access to a system bundle
     *
     * @param bundleId
     * @param session
     * @return true if the user confirm
     * @throws IOException
     */
    public static boolean accessToSystemBundleIsAllowed(long bundleId, CommandSession session) throws IOException {
        for (; ; ) {
            ConsoleReader reader = (ConsoleReader) session.get(".jline.reader");
            String msg = "You are about to access system bundle " + bundleId + ".  Do you wish to continue (yes/no): ";
            String str = reader.readLine(msg);
            if ("yes".equalsIgnoreCase(str)) {
                return true;
            }
            if ("no".equalsIgnoreCase(str)) {
                return false;
            }
        }
    }

    public static String loadClassPathResource(Class<?> clazz, String path) {
        InputStream is = clazz.getResourceAsStream(path);
        if (is == null) {
            is = clazz.getClassLoader().getResourceAsStream(path);
        }
        if (is == null) {
            return "Unable to load description from " + path;
        }

        try {
            Reader r = new InputStreamReader(is);
            StringWriter sw = new StringWriter();
            int c;
            while ((c = r.read()) != -1) {
                sw.append((char) c);
            }
            return sw.toString();
        } catch (IOException e) {
            return "Unable to load description from " + path;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public static boolean getBoolean(CommandSession session, String name) {
        Object s = session.get(name);
        if (s == null) {
            s = System.getProperty(name);
        }
        if (s == null) {
            return false;
        }
        if (s instanceof Boolean) {
            return (Boolean) s;
        }
        return Boolean.parseBoolean(s.toString());
    }

    public static void logException(CommandSession session, Throwable t) {
        try {
            boolean isCommandNotFound = "org.apache.felix.gogo.runtime.CommandNotFoundException".equals(t.getClass().getName());
            if (isCommandNotFound) {
                LOGGER.debug("Unknown command entered", t);
            } else if (t instanceof CommandException) {
                LOGGER.debug("Command exception (Undefined option, ...)", t);
            } else {
                LOGGER.error("Exception caught while executing command", t);
            }
            session.put(SessionProperties.LAST_EXCEPTION, t);
            if (t instanceof CommandException) {
                session.getConsole().println(((CommandException) t).getNiceHelp());
            } else if (isCommandNotFound) {
                String str = Ansi.ansi()
                        .fg(Ansi.Color.RED)
                        .a("Command not found: ")
                        .a(Ansi.Attribute.INTENSITY_BOLD)
                        .a(t.getClass().getMethod("getCommand").invoke(t))
                        .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                        .fg(Ansi.Color.DEFAULT).toString();
                session.getConsole().println(str);
            }
            if (getBoolean(session, SessionProperties.PRINT_STACK_TRACES)) {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                t.printStackTrace(session.getConsole());
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            } else if (!(t instanceof CommandException) && !isCommandNotFound) {
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.RED).toString());
                session.getConsole().println("Error executing command: "
                        + (t.getMessage() != null ? t.getMessage() : t.getClass().getName()));
                session.getConsole().print(Ansi.ansi().fg(Ansi.Color.DEFAULT).toString());
            }
        } catch (Exception ignore) {
            // ignore
        }
    }

}
