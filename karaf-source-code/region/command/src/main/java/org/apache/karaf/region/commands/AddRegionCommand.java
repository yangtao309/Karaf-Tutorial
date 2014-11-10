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
package org.apache.karaf.region.commands;

import java.util.List;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.eclipse.equinox.region.RegionDigraph;

@Command(scope = "region", name = "region-add", description = "Adds a list of regions to the region digraph service.")
public class AddRegionCommand extends RegionCommandSupport {

    @Argument(index = 0, name = "name", description = "Regions to add to the region digraph service separated by whitespaces.", required = true, multiValued = true)
    List<String> regions;

    protected void doExecute(RegionDigraph regionDigraph) throws Exception {
        for (String region : regions) {
            regionDigraph.createRegion(region);
        }
    }
}
