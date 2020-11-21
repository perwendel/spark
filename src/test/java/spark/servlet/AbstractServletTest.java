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

import static spark.Service.ignite;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Service;
import spark.Spark;
import spark.util.SparkTestUtil;

abstract class AbstractServletTest {
    private static Server server;

    protected static SparkTestUtil testUtil;

    private static ExecutorService executorService;

    private static final String ROOT_CONTEXT = "/somepath";

    private static final int PORT = 9393;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServletTest.class);

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private static File tmpExternalFile;

    private final String subContext;

    static void createTempStaticFile(String subContext, String fileName) {
        Service service = ignite();
        service.externalStaticFileLocation(TMP_DIR);
        service.staticFileLocation("/public");

        Path filterPath = Paths.get(TMP_DIR, subContext);
        filterPath.toFile()
                .mkdirs();

        try {
            tmpExternalFile = new File(filterPath.toFile(), fileName);
            FileWriter writer = new FileWriter(tmpExternalFile, false);
            writer.write("Content of external file");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    AbstractServletTest(String subContext) {
        this.subContext = ROOT_CONTEXT + "/" + subContext;
    }

    protected abstract String getExternalFileName();

    static void absSetup() throws InterruptedException {
        testUtil = new SparkTestUtil(PORT);

        server = new Server();
        ServerConnector connector = new ServerConnector(server);

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] {connector});

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath(ROOT_CONTEXT);
        bb.setWar("src/test/webapp");

        server.setHandler(bb);
        CountDownLatch latch = new CountDownLatch(1);

        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                LOGGER.info(">>> STARTING EMBEDDED JETTY SERVER for jUnit testing of SparkFilter");
                server.start();
                latch.countDown();
                System.in.read();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(100);
            }
        });

        latch.await();
    }

    static void absTearDown() {
        Spark.stop();
        if (tmpExternalFile != null) {
            LOGGER.debug("tearDown().deleting: " + tmpExternalFile);
            tmpExternalFile.delete();
        }

        LOGGER.info(">>> STOPPING EMBEDDED JETTY SERVER");
        try {
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdownNow();
    }

    @Test
    public void testGetHi() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", subContext + "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @Test
    public void testHiHead() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("HEAD", subContext + "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }

    @Test
    public void testGetHiAfterFilter() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", subContext + "/hi", null);
        Assert.assertTrue(response.headers.get("after")
                .contains("foobar"));
    }

    @Test
    public void testGetRoot() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", subContext + "/", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }

    @Test
    public void testEchoParam1() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", subContext + "/shizzy", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", subContext + "/gunit", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test
    public void testUnauthorized() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod("GET",
                subContext + "/protected/resource",
                null);
        Assert.assertTrue(urlResponse.status == 401);
    }

    @Test
    public void testNotFound() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod("GET",
                subContext + "/no/resource",
                null);
        Assert.assertTrue(urlResponse.status == 404);
    }

    @Test
    public void testPost() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("POST",
                subContext + "/poster",
                "Fo shizzy");
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }
}
