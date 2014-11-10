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

EMBEDDING KARAF IN A WEB APPLICATION
====================================

Purpose
-------
To embed Karaf in a web application.


Prerequisites for Running the Example
-------------------------------------
You must have the following installed on your machine:

  - JDK 1.6 or higher

  - Maven 3.0.3 or higher


Building and Deploying
----------------------
You can build and deploy this example in two ways:

- A. Using Jetty: Quick and Easy
  This option is useful if you want to see the example up and
  running quickly.
   
- B. Using Your Favorite Web Container
  This option is useful if you want to see Karaf running
  as a web application inside your favorite web container.


A. Using Jetty: Quick and Easy
------------------------------
To build the example and deploy to Jetty, complete the
following steps:

1. In a command prompt/shell, change to the directory
   that contains this README.txt file.

2. Enter the following Maven command:

     mvn package jetty:run

This Maven command builds the example web application, starts
Jetty and deploys the web application to Jetty. Once complete,
you should see the following printed to the console:

[INFO] Started Jetty Server
[INFO] Starting scanner at interval of 10 seconds.

Running a Client
----------------
To test the example, you can use the Apache Karaf client
to connect to the server and issue a Karaf command. For example,
try executing the "features:list" command as follows:

1. In a command prompt/shell, change to your product
   installation directory.

2. Run the following commands:

    Unix:
        ${KARAF_HOME}/bin/client feature:list

    Windows:
        %KARAF_HOME%\bin\client.bat feature:list

    Using JDK:
        java -jar lib/bin/karaf-client.jar features:list

In this case, you should see output similar to the following:

(abbreviated output below)

State         Version           Name              Repository                   Description
[uninstalled] [${version} ] wrapper           standard-${version}      Provide OS integration
[uninstalled] [${version} ] obr               standard-${version}      Provide OSGi Bundle Repository (OBR) support
[uninstalled] [${version} ] config            standard-${version}      Provide OSGi ConfigAdmin support
[uninstalled] [${version} ] region            standard-${version}      Provide Region commands
[uninstalled] [7.5.4.v20111024] jetty             standard-${version}      Provide Jetty engine support
[uninstalled] [${version} ] http              standard-${version}      Implementation of the OSGI HTTP Service
[uninstalled] [${version} ] http-whiteboard   standard-${version}      Provide HTTP Whiteboard pattern support
[uninstalled] [${version} ] war               standard-${version}      Turn Karaf as a full WebContainer
[uninstalled] [${version} ] deployers         standard-${version}      Provide Karaf deployer
[uninstalled] [${version} ] kar               standard-${version}      Provide KAR (KARaf archive) support
[uninstalled] [${version} ] webconsole-base   standard-${version}      Base support of the Karaf WebConsole
[uninstalled] [${version} ] webconsole        standard-${version}      Karaf WebConsole for administration and monitoring
[uninstalled] [${version} ] ssh               standard-${version}      Provide a SSHd server on Karaf


B. Using Your Favorite Web Container
------------------------------------
You can deploy the web application to your favorite web
container, by completing the following steps:

1. In a command prompt/shell, change to the directory
   that contains this README.txt file.
   
2. Enter the following command:

     mvn package
     
Maven builds the web application, web-${version}.war, and
saves it in the target directory of this example. Deploy this
WAR file to your favorite web container. Once the application
is running, you can test it using the Apache Karaf client
as described in the "Running a Client" section above.
