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
package org.apache.karaf.jdbc.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;

@Command(scope = "jdbc", name = "execute", description = "Execute a SQL command on a given JDBC datasource")
public class ExecuteCommand extends JdbcCommandSupport {

    @Argument(index = 0, name = "datasource", description = "The JDBC datasource", required = true, multiValued = false)
    String datasource;

    @Argument(index = 1, name = "command", description = "The SQL command to execute", required = true, multiValued = false)
    String command;

    public Object doExecute() throws Exception {
        this.getJdbcService().execute(datasource, command);
        return null;
    }

}
