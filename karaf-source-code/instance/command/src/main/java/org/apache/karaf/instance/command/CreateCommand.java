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
package org.apache.karaf.instance.command;

import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.instance.core.InstanceSettings;

/**
 * Creates a new instance.
 */
@Command(scope = "instance", name = "create", description = "Creates a new container instance.")
public class CreateCommand extends InstanceCommandSupport
{
    @Option(name = "-s", aliases = {"--ssh-port"}, description = "Port number for remote secure shell connection", required = false, multiValued = false)
    int sshPort = 0;

    @Option(name = "-r", aliases = {"-rr", "--rmi-port", "--rmi-registry-port"}, description = "Port number for RMI registry connection", required = false, multiValued = false)
    int rmiRegistryPort = 0;

    @Option(name = "-rs", aliases = {"--rmi-server-port"}, description = "Port number for RMI server connection", required = false, multiValued = false)
    int rmiServerPort = 0;

    @Option(name = "-l", aliases = {"--location"}, description = "Location of the new container instance in the file system", required = false, multiValued = false)
    String location;

    @Option(name = "-o", aliases = {"--java-opts"}, description = "JVM options to use when launching the instance", required = false, multiValued = false)
    String javaOpts;
    
    @Option(name = "-f", aliases = {"--feature"},
            description = "Initial features. This option can be specified multiple times to enable multiple initial features", required = false, multiValued = true)
    List<String> features;
    
    @Option(name = "-furl", aliases = {"--featureURL"}, 
            description = "Additional feature descriptor URLs. This option can be specified multiple times to add multiple URLs", required = false, multiValued = true)
    List<String> featureURLs;

    @Option(name = "-v", aliases = {"--verbose"}, description = "Display actions performed by the command (disabled by default)", required = false, multiValued = false)
    boolean verbose = false;

    @Option(name = "-a", aliases = {"--address"}, description = "IP address of the new container instance running on (when virtual IP is used)", required = false, multiValued = false)
    String address = "0.0.0.0";

    @Argument(index = 0, name = "name", description="The name of the new container instance", required = true, multiValued = false)
    String instance = null;

    protected Object doExecute() throws Exception {
        InstanceSettings settings = new InstanceSettings(sshPort, rmiRegistryPort, rmiServerPort, location, javaOpts, featureURLs, features, address);
        getInstanceService().createInstance(instance, settings, verbose);
        return null;
    }

}
