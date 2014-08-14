package spark.servlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static spark.util.SparkTestUtil.sleep;

public class ServletTest {

    private static final String SOMEPATH = "/somepath";
    private static final int PORT = 9393;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletTest.class);
    static final Server server = new Server();

    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() {
        testUtil = new SparkTestUtil(PORT);

        final Server server = new Server();
        ServerConnector connector = new ServerConnector(server);

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] {connector});

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath(SOMEPATH);
        bb.setWar("src/test/webapp");

        server.setHandler(bb);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info(">>> STARTING EMBEDDED JETTY SERVER for jUnit testing of SparkFilter");
                    server.start();
                    System.in.read();
                    LOGGER.info(">>> STOPPING EMBEDDED JETTY SERVER");
                    server.stop();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(100);
                }
            }
        }).start();

        sleep(1000);
    }

    @Test
    public void testGetHi() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() {
        try {
            UrlResponse response = testUtil.doMethod("HEAD", SOMEPATH + "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetHiAfterFilter() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
            Assert.assertTrue(response.headers.get("after").contains("foobar"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello Root!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam1() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/shizzy", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: shizzy", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam2() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/gunit", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: gunit", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnauthorized() throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethod("GET", SOMEPATH + "/protected/resource", null);
            Assert.assertTrue(urlResponse.status == 401);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNotFound() throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethod("GET", SOMEPATH + "/no/resource", null);
            Assert.assertTrue(urlResponse.status == 404);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPost() {
        try {
            UrlResponse response = testUtil.doMethod("POST", SOMEPATH + "/poster", "Fo shizzy");
            Assert.assertEquals(201, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticResource() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/css/style.css", null);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("Content of css file"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticWelcomeResource() {
        try {
            UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/pages/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("<html><body>Hello Static World!</body></html>"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }
}
