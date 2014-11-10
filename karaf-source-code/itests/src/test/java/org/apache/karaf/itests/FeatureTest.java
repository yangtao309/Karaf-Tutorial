/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.itests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;

import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class FeatureTest extends KarafTestSupport {

    @Test
    public void bootFeatures() throws Exception {
        assertFeaturesInstalled("standard", "config", "region", "package", "kar", "management");
    }

    @Test
    public void listCommand() throws Exception {
        String featureListOutput = executeCommand("feature:list");
        System.out.println(featureListOutput);
        assertFalse(featureListOutput.isEmpty());
        featureListOutput = executeCommand("feature:list -i");
        System.out.println(featureListOutput);
        assertFalse(featureListOutput.isEmpty());
    }

    @Test
    public void listViaMBean() throws Exception {
        JMXConnector connector = null;
        try {
            connector = this.getJMXConnector();
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName name = new ObjectName("org.apache.karaf:type=feature,name=root");
            TabularData features = (TabularData) connection.getAttribute(name, "Features");
            assertTrue(features.size() > 0);
        } finally {
        	close(connector);
        }
    }

    @Test
    public void installUninstallCommand() throws Exception {
        System.out.println(executeCommand("feature:install eventadmin", new RolePrincipal("admin")));
        assertFeatureInstalled("eventadmin");
        System.out.println(executeCommand("feature:uninstall eventadmin", new RolePrincipal("admin")));
        assertFeatureNotInstalled("eventadmin");
    }

    @Test
    public void installUninstallViaMBean() throws Exception {
        JMXConnector connector = null;
        try {
            connector = this.getJMXConnector();
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName name = new ObjectName("org.apache.karaf:type=feature,name=root");
            connection.invoke(name, "installFeature", new Object[] { "eventadmin" }, new String[]{ "java.lang.String" });
            connection.invoke(name, "uninstallFeature", new Object[] { "eventadmin" }, new String[]{ "java.lang.String" });
        } finally {
        	close(connector);
        }
    }

    @Test
    public void repoAddRemoveCommand() throws Exception {
        System.out.println(executeCommand("feature:repo-add mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.2.4/xml/features"));
        assertContains("apache-karaf-cellar", executeCommand("feature:repo-list"));
        System.out.println(executeCommand("feature:repo-remove mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.2.4/xml/features"));
        assertContainsNot("apache-karaf-cellar", executeCommand("feature:repo-list"));
    }

    @Test
    public void repoAddRemoveViaMBean() throws Exception {
        JMXConnector connector = null;
        try {
            connector = this.getJMXConnector();
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            ObjectName name = new ObjectName("org.apache.karaf:type=feature,name=root");
            connection.invoke(name, "addRepository", new Object[] { "mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.2.4/xml/features" }, new String[]{ "java.lang.String" });
            connection.invoke(name, "removeRepository", new Object[] { "mvn:org.apache.karaf.cellar/apache-karaf-cellar/2.2.4/xml/features" }, new String[]{ "java.lang.String" });
        } finally {
        	close(connector);
        }
    }

}
