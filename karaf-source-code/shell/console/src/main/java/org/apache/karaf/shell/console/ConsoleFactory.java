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
package org.apache.karaf.shell.console;

import java.io.InputStream;
import java.io.PrintStream;

import javax.security.auth.Subject;

import jline.Terminal;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.threadio.ThreadIO;

public interface ConsoleFactory {
    Console create(CommandProcessor processor, ThreadIO threadIO, InputStream in, PrintStream out, PrintStream err, final Terminal term, String encoding, Runnable closeCallback);
    Console createLocal(CommandProcessor processor, ThreadIO threadIO, Terminal terminal, String encoding, Runnable closeCallback);
    void startConsoleAs(Console console, Subject subject, String threadName);
}
