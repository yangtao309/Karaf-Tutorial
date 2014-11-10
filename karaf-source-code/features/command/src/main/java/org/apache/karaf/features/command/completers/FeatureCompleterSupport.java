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
package org.apache.karaf.features.command.completers;

import java.util.List;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;

/**
 * Base completer for feature commands.
 */
public abstract class FeatureCompleterSupport implements Completer {

    /**
     * Feature service.
     */
    protected FeaturesService featuresService;

    public void setFeaturesService(FeaturesService featuresService) {
        this.featuresService = featuresService;
    }

    public int complete(final String buffer, final int cursor, @SuppressWarnings("rawtypes") final List candidates) {
        StringsCompleter delegate = new StringsCompleter();
        try {
            for (Feature feature : featuresService.listFeatures()) {
                if (acceptsFeature(feature)) {
                    delegate.getStrings().add(feature.getName());
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return delegate.complete(buffer, cursor, candidates);
    }

    /**
     * Method for filtering features.
     *
     * @param feature The feature.
     * @return True if feature should be available in completer.
     */
    protected abstract boolean acceptsFeature(Feature feature);
}
