/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.shell.console.impl.jline;

import jline.NoInterruptUnixTerminal;
import jline.Terminal;

public class TerminalFactory {

    private Terminal term;

    public synchronized Terminal getTerminal() throws Exception {
        if (term == null) {
            init();
        }
        return term;
    }

    public void init() throws Exception {
        jline.TerminalFactory.registerFlavor(jline.TerminalFactory.Flavor.UNIX, NoInterruptUnixTerminal.class);
        term = jline.TerminalFactory.create();
    }

    public synchronized void destroy() throws Exception {
        if (term != null) {
            term.restore();
            term = null;
        }
    }

}
