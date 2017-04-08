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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.examples.exception.NotFoundException;
import spark.util.SparkTestUtil;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.staticFiles;

/**
 * Test static files
 */
public class StaticFilesMemberTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesMemberTest.class);

    private static final String FO_SHIZZY = "Fo shizzy";
    private static final String NOT_FOUND_BRO = "Not found bro";

    private static final String EXTERNAL_FILE_NAME_HTML = "externalFile.html";

    private static final String CONTENT_OF_EXTERNAL_FILE = "Content of external file";

    private static SparkTestUtil testUtil;

    private static File tmpExternalFile;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
        if (tmpExternalFile != null) {
            LOGGER.debug("tearDown().deleting: " + tmpExternalFile);
            tmpExternalFile.delete();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), EXTERNAL_FILE_NAME_HTML);

        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write(CONTENT_OF_EXTERNAL_FILE);
        writer.flush();
        writer.close();

        staticFiles.location("/public");
        staticFiles.externalLocation(System.getProperty("java.io.tmpdir"));

        get("/hello", (q, a) -> FO_SHIZZY);

        get("/*", (q, a) -> {
            throw new NotFoundException();
        });

        exception(NotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.body(NOT_FOUND_BRO);
        });

        Spark.awaitInitialization();
    }

    @Test
    public void testStaticFileCssStyleCss() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of css file", response.body);

        testGet();
    }

    @Test
    public void testStaticFilePagesIndexHtml() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("<html><body>Hello Static World!</body></html>", response.body);

        testGet();
    }

    @Test
    public void testStaticFilePageHtml() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/page.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("<html><body>Hello Static Files World!</body></html>", response.body);

        testGet();
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);

        testGet();
    }

    @Test
    public void testStaticFileHeaders() throws Exception {
        staticFiles.headers(new HashMap() {
            {
                put("Server", "Microsoft Word");
                put("Cache-Control", "private, max-age=600");
            }
        });
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals("Microsoft Word", response.headers.get("Server"));
        Assert.assertEquals("private, max-age=600", response.headers.get("Cache-Control"));

        testGet();
    }

    @Test
    public void testStaticFileExpireTime() throws Exception {
        staticFiles.expireTime(600);
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/pages/index.html", null);
        Assert.assertEquals("private, max-age=600", response.headers.get("Cache-Control"));

        testGet();
    }

    /**
     * Used to verify that "normal" functionality works after static files mapping
     */
    private static void testGet() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hello", "");

        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(FO_SHIZZY));
    }

    @Test
    public void testExceptionMapping404() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/filethatdoesntexist.html", null);

        Assert.assertEquals(404, response.status);
        Assert.assertEquals(NOT_FOUND_BRO, response.body);
    }

}
