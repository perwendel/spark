/*
 * Copyright 2016- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.servlet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;

public class FilterTest extends AbstractServletTest {
    public FilterTest() {
        super("filter");
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        absSetup();
    }

    @AfterClass
    public static void tearDown() {
        absTearDown();
    }

    @Override
    protected String getExternalFileName() {
        return MyApp.EXTERNAL_FILE;
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        createTempStaticFile("filter", getExternalFileName());
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET",
                "/somepath/filter/" + getExternalFileName(),
                null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }
}