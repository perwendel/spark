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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.util.SparkTestUtil;

import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Test static files
 */
public class StaticFilesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesTest.class);
    private static final String FO_SHIZZY = "Fo shizzy";

    private static final String EXTERNAL_FILE_NAME_HTML = "externalFile.html";
    private static final String EXTERNAL_FILE_NAME_CSS = "stylish.css";
    public static final String CONTENT_OF_EXTERNAL_FILE = "Content of external file";
    public static final String SUB_DIR = "subdir";

    private static SparkTestUtil testUtil;
    private static File tmpExternalFile;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
        if (tmpExternalFile != null) {
            tmpExternalFile.delete();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), EXTERNAL_FILE_NAME_HTML);
        createExternalSubDirectoryAndFile(System.getProperty("java.io.tmpdir") + SUB_DIR);

        System.out.println("System.getProperty(\"java.io.tmpdir\" = " + System.getProperty("java.io.tmpdir"));

        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write(CONTENT_OF_EXTERNAL_FILE);
        writer.flush();
        writer.close();

        staticFileLocation("/public");
        externalStaticFileLocation(System.getProperty("java.io.tmpdir"));

        get("/hello", (q, a) -> FO_SHIZZY);

        Spark.awaitInitialization();
    }

    private static void createExternalSubDirectoryAndFile(String directoryName) throws IOException {
        File directory = new File(directoryName);

        // if the directory does not exist, create it
        if (!directory.exists()) {
            System.out.println("creating directory: " + directoryName);
            boolean result = false;

            try {
                directory.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

        File tmpExternalFile = new File(directoryName, EXTERNAL_FILE_NAME_CSS);
        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write(CONTENT_OF_EXTERNAL_FILE);
        writer.flush();
        writer.close();
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
    public void testExternalStaticFileSubdirStyleCss() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/" + SUB_DIR + "/stylish.css", null);

        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);

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

}
