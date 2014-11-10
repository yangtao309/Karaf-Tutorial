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
package org.apache.karaf.main.util;

import org.apache.karaf.main.util.StringMap;

import junit.framework.TestCase;

/**
 * Test cased for {@link org.apache.karaf.main.util.StringMap}
 */
public class StringMapTest extends TestCase {

    public void testCaseInsensitiveStringMap() throws Exception {
        StringMap sm = new StringMap(false);
        String value = new String("value");
        sm.put("key", value);
        Object result = sm.get("Key");
        assertFalse(result == null);
        assertTrue(result.equals(value));
    }

}
