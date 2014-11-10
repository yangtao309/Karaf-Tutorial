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
package org.apache.karaf.itests.features;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EnterpriseFeaturesTest extends KarafTestSupport {

    @Test
    public void installTransactionFeature() throws Exception {
        installAssertAndUninstallFeature("transaction");
    }

    @Test
    public void installJpaFeature() throws Exception {
    	installAssertAndUninstallFeature("jpa");
    }

    @Test
    public void installOpenJpaFeature() throws Exception {
        installAssertAndUninstallFeature("openjpa");
    }

    @Test
    public void installHibernateFeature() throws Exception {
        installAssertAndUninstallFeature("hibernate");
    }

    @Test
    public void installHibernateEnversFeature() throws Exception {
        installAssertAndUninstallFeature("hibernate-envers");
    }

    @Test
    public void installHibernateValidatorFeature() throws Exception {
        installAssertAndUninstallFeature("hibernate-validator");
    }

    @Test
    public void installJndiFeature() throws Exception {
    	installAssertAndUninstallFeature("jndi");
    }

    @Test
    public void installJdbcFeature() throws Exception {
        installAssertAndUninstallFeature("jdbc");
    }

    @Test
    public void installJmsFeature() throws Exception {
        installAssertAndUninstallFeature("jms");
    }

    @Test
    public void installOpenWebBeansFeature() throws Exception {
        installAssertAndUninstallFeature("openwebbeans");
    }

    @Test
    public void installWeldFeature() throws Exception {
        installAssertAndUninstallFeature("weld");
    }

    @Test
    public void installApplicationWithoutIsolationFeature() throws Exception {
    	installAssertAndUninstallFeature("application-without-isolation");
    }

}
