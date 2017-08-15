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
package spark.staticfiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;
import spark.examples.exception.NotFoundException;
import spark.util.SparkTestUtil;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.staticFiles;

/**
 * Test external static files
 */
public class StaticFilesTestExternal {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFilesTestExternal.class);

    private static final String FO_SHIZZY = "Fo shizzy";
    private static final String NOT_FOUND_BRO = "Not found bro";

    private static final String EXTERNAL_FILE_NAME_HTML = "externalFile.html";

    private static final String CONTENT_OF_EXTERNAL_FILE = "Content of external file";

    private static SparkTestUtil testUtil;

    private static File tmpExternalFile1;
    private static File tmpExternalFile2;
    private static File folderOutsideStaticFiles;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
        if (tmpExternalFile1 != null) {
            LOGGER.debug("tearDown(). Deleting tmp files");
            tmpExternalFile1.delete();
            tmpExternalFile2.delete();
            folderOutsideStaticFiles.delete();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        String directoryRoot = System.getProperty("java.io.tmpdir") + "sparkish";
        new File(directoryRoot).mkdirs();

        tmpExternalFile1 = new File(directoryRoot, EXTERNAL_FILE_NAME_HTML);

        FileWriter writer = new FileWriter(tmpExternalFile1);
        writer.write(CONTENT_OF_EXTERNAL_FILE);
        writer.flush();
        writer.close();

        File root = new File(directoryRoot);

        folderOutsideStaticFiles = new File(root.getAbsolutePath() + "/../dumpsterstuff");
        folderOutsideStaticFiles.mkdirs();

        String newFilePath = root.getAbsolutePath() + "/../dumpsterstuff/Spark.class";
        tmpExternalFile2 = new File(newFilePath);
        tmpExternalFile2.createNewFile();

        staticFiles.externalLocation(directoryRoot);

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
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = doGet("/externalFile.html");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("text/html", response.headers.get("Content-Type"));
        Assert.assertEquals(CONTENT_OF_EXTERNAL_FILE, response.body);

        testGet();
    }

    @Test
    public void testDirectoryTraversalProtectionExternal() throws Exception {
        String path = "/" + URLEncoder.encode("..\\..\\spark\\", "UTF-8") + "Spark.class";
        SparkTestUtil.UrlResponse response = doGet(path);

        Assert.assertEquals(404, response.status);
        Assert.assertEquals(NOT_FOUND_BRO, response.body);

        testGet();
    }

    private static void testGet() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hello", "");

        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(FO_SHIZZY));
    }

    private SparkTestUtil.UrlResponse doGet(String fileName) throws Exception {
        return testUtil.doMethod("GET", fileName, null);
    }

}
