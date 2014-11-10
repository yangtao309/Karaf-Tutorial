/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.jaas.command;

import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.jaas.modules.BackingEngineService;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import javax.security.auth.login.AppConfigurationEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

public abstract class JaasCommandSupport extends OsgiCommandSupport {

    public static final String JAAS_REALM = "JaasCommand.REALM";
    public static final String JAAS_ENTRY = "JaasCommand.ENTRY";
    public static final String JAAS_CMDS = "JaasCommand.COMMANDS";

    private List<JaasRealm> realms;

    protected BackingEngineService backingEngineService;

    protected abstract Object doExecute(BackingEngine engine) throws Exception;

    /**
     * Add the command to the command queue.
     *
     * @return
     * @throws Exception
     */
    protected Object doExecute() throws Exception {
        JaasRealm realm = (JaasRealm) session.get(JAAS_REALM);
        AppConfigurationEntry entry = (AppConfigurationEntry) session.get(JAAS_ENTRY);
        @SuppressWarnings("unchecked")
        Queue<JaasCommandSupport> commandQueue = (Queue<JaasCommandSupport>) session.get(JAAS_CMDS);

        if (realm != null && entry != null) {
            if (commandQueue != null) {
                commandQueue.add(this);
            }
        } else {
            System.err.println("No JAAS Realm / Module has been selected");
        }
        return null;
    }

    public List<JaasRealm> getRealms() {
        return getRealms(false);
    }

    public List<JaasRealm> getRealms(boolean hidden) {
        if (hidden) {
            return realms;
        } else {
            Map<String, JaasRealm> map = new TreeMap<String, JaasRealm>();
            for (JaasRealm realm : realms) {
                if (!map.containsKey(realm.getName())
                        || realm.getRank() > map.get(realm.getName()).getRank()) {
                    map.put(realm.getName(), realm);
                }
            }
            return new ArrayList<JaasRealm>(map.values());
        }
    }

    public void setRealms(List<JaasRealm> realms) {
        this.realms = realms;
    }

    public BackingEngineService getBackingEngineService() {
        return backingEngineService;
    }

    public void setBackingEngineService(BackingEngineService backingEngineService) {
        this.backingEngineService = backingEngineService;
    }

}
