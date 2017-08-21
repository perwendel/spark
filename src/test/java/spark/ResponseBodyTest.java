/*
 * Copyright 2016 - Per Wendel
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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;

import static org.junit.Assert.assertEquals;
import static spark.Spark.after;
import static spark.Spark.get;

/**
 * Validates and shows the "rules" for how response "body" is set.
 */
public class ResponseBodyTest {

    public static final String HELLO = "/hello";
    public static final String SPECIAL = "/special";
    public static final String PORAKATIKAOKAO = "/porakatikaokao";
    public static final String MAXIME = "/maxime";

    public static final String HELLO_WORLD = "Hello World!";
    public static final String XIDXUS = "xidxus";
    public static final String $11AB = "$11ab";
    public static final String GALLUS_SCANDALUM = "gallus scandalum";

    private static SparkTestUtil http;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        http = new SparkTestUtil(4567);

        get(HELLO, (q, a) -> HELLO_WORLD);

        after(HELLO, (q, a) -> {
            String body = a.body();
            assertEquals(HELLO_WORLD, body);
        });

        get(SPECIAL, (q, a) -> {
            a.body(XIDXUS);
            return "";
        });

        after(SPECIAL, (q, a) -> {
            String body = a.body();
            assertEquals(XIDXUS, body);
        });

        get(PORAKATIKAOKAO, (q, a) -> {
            a.body(GALLUS_SCANDALUM);
            return null;
        });

        after(PORAKATIKAOKAO, (q, a) -> {
            String body = a.body();
            assertEquals(GALLUS_SCANDALUM, body);
        });

        get(MAXIME, (q, a) -> {
            a.body(XIDXUS);
            return $11AB;
        });

        after(MAXIME, (q, a) -> {
            String body = a.body();
            assertEquals($11AB, body);
        });

        Spark.awaitInitialization();
    }

    @Test
    public void testHELLO() {
        try {
            SparkTestUtil.UrlResponse response = http.get(HELLO);
            assertEquals(200, response.status);
            assertEquals(HELLO_WORLD, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSPECIAL() {
        try {
            SparkTestUtil.UrlResponse response = http.get(SPECIAL);
            assertEquals(200, response.status);
            assertEquals(XIDXUS, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPORAKATIKAOKAO() {
        try {
            SparkTestUtil.UrlResponse response = http.get(PORAKATIKAOKAO);
            assertEquals(200, response.status);
            assertEquals(GALLUS_SCANDALUM, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testMAXIME() {
        try {
            SparkTestUtil.UrlResponse response = http.get(MAXIME);
            assertEquals(200, response.status);
            assertEquals($11AB, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
