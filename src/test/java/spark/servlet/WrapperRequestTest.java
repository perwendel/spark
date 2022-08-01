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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;
import spark.util.SparkTestUtil;

public class WrapperRequestTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WrapperRequestTest.class);

    private static final String CONTEXT_PATH = "/wrapperpath";

    private static final String SUB_CONTEXT = CONTEXT_PATH + "/servlet2";

    private static Server server;

    protected static SparkTestUtil testUtil;

    private static ExecutorService executorService;

    private static final int PORT = 9393;

    @BeforeClass
    public static void setup() throws InterruptedException {
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
        bb.setContextPath(CONTEXT_PATH);
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
                try {
                    latch.countDown();
                    server.stop();
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
                throw new RuntimeException(e);
            }
        });

        latch.await();
    }

    @AfterClass
    public static void tearDown() {
        Spark.stop();

        LOGGER.info(">>> STOPPING EMBEDDED JETTY SERVER");
        try {
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdownNow();
    }

    @Before
    public void init() {
        ignite().staticFileLocation("/public");
    }

    @Test
    public void testStaticResource() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET",
                SUB_CONTEXT + "/css/style.css",
                null);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("Content of css file"));
    }

    @Test
    public void testStaticWelcomeResource() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET",
                SUB_CONTEXT + "/pages/",
                null);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains("<html><body>Hello Static World!</body></html>"));
    }
}
