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
package org.apache.karaf.packages.core;

import java.util.List;
import java.util.SortedMap;

public interface PackageService {

	/**
	 * Gets the simplified package exports of a bundle. This does not show the 
	 * package versions.
	 * 
	 * @param bundleId
	 * @return
	 */
    List<String> getExports(long bundleId);

    List<String> getImports(long bundleId);

	/**
	 * Gets a map of all exported packages with their version and the bundles that export them
	 * The key is in the form packagename:version.
	 * 
	 * @return 
	 */
    SortedMap<String, PackageVersion> getExports();

    /**
     * Gets a map of all package imports. 
     * The key is the import filter.
     *  
     * @return
     */
    SortedMap<String, PackageRequirement> getImports();

}
