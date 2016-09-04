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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;

import static spark.Spark.get;
import static spark.Spark.redirect;

/**
 * Tests the redirect utility methods in {@link spark.Redirect}
 */
public class RedirectTest {

    private static final String REDIRECTED = "Redirected";

    private static SparkTestUtil testUtil;

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);
        testUtil.setFollowRedirectStrategy(301, 302); // don't set the others to be able to verify affect of Redirect.Status

        get("/hello", (request, response) -> REDIRECTED);

        redirect.get("/hi", "/hello");
        redirect.post("/hi", "/hello");
        redirect.put("/hi", "/hello");
        redirect.delete("/hi", "/hello");
        redirect.any("/any", "/hello");

        redirect.get("/hiagain", "/hello", Redirect.Status.USE_PROXY);
        redirect.post("/hiagain", "/hello", Redirect.Status.USE_PROXY);
        redirect.put("/hiagain", "/hello", Redirect.Status.USE_PROXY);
        redirect.delete("/hiagain", "/hello", Redirect.Status.USE_PROXY);
        redirect.any("/anyagain", "/hello", Redirect.Status.USE_PROXY);

        Spark.awaitInitialization();
    }

    @Test
    public void testRedirectGet() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectPost() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/hi", "");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectPut() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/hi", "");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectDelete() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectAnyGet() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/any", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectAnyPut() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/any", "");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectAnyPost() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/any", "");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectAnyDelete() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/any", "");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(REDIRECTED, response.body);
    }

    @Test
    public void testRedirectGetWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hiagain", null);
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectPostWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/hiagain", "");
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectPutWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/hiagain", "");
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectDeleteWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/hiagain", null);
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectAnyGetWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/anyagain", null);
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectAnyPostWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/anyagain", "");
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectAnyPutWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/anyagain", "");
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

    @Test
    public void testRedirectAnyDeleteWithSpecificCode() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/anyagain", null);
        Assert.assertEquals(Redirect.Status.USE_PROXY.intValue(), response.status);
    }

}
