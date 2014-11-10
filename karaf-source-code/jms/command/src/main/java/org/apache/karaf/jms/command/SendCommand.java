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
package org.apache.karaf.jms.command;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;

@Command(scope = "jms", name = "send", description = "Send a message to ")
public class SendCommand extends JmsConnectionCommandSupport {

    @Argument(index = 1, name = "queue", description = "The JMS queue name", required = true, multiValued = false)
    String queue;

    @Argument(index = 2, name = "message", description = "The JMS message content", required = true, multiValued = false)
    String message;

    @Option(name = "-r", aliases = { "--replyTo" }, description = "Set the message ReplyTo", required = false, multiValued = false)
    String replyTo;

    public Object doExecute() throws Exception {
        getJmsService().send(connectionFactory, queue, message, replyTo, username, password);
        return null;
    }

}
