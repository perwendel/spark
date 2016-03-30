/*
 * Copyright 2015 - Per Wendel
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
package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.examples.gzip.GzipClient;
import spark.examples.gzip.GzipExample;
import spark.util.SparkTestUtil;

import static org.junit.Assert.assertEquals;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

/**
 * Tests the GZIP compression support in Spark.
 */
public class GzipTest {

    @BeforeClass
    public static void setup() {
        GzipExample.addStaticFileLocation();
        GzipExample.addRoutes();
        awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        stop();
    }

    @Test
    public void checkGzipCompression() throws Exception {
        String decompressed = GzipExample.getAndDecompress();
        assertEquals(GzipExample.CONTENT, decompressed);
    }

    @Test
    public void testStaticFileCssStyleCss() throws Exception {
        String decompressed = GzipClient.getAndDecompress("http://localhost:4567/css/style.css");
        Assert.assertEquals("Content of css file", decompressed);
        testGet();
    }

    /**
     * Used to verify that "normal" functionality works after static files mapping
     */
    private static void testGet() throws Exception {
        SparkTestUtil testUtil = new SparkTestUtil(4567);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hello", "");

        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(GzipExample.FO_SHIZZY));
    }

}
