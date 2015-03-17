package spark.servlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.util.SparkTestUtil;

import java.util.concurrent.TimeUnit;

import static spark.util.SparkTestUtil.sleep;

/**
 * Set of all Servlet/Filter-related test methods.
 * Delegates server and test util startup to subclasses
 *
 * @author Andrei Varabyeu
 */
abstract public class BaseServletTest {

    private static final int PORT = 9393;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletTest.class);
    protected static final String SOMEPATH = "/somepath";
    protected static final String TEST_APP_CLASS = "spark.servlet.MyApp";
    static Server server = new Server();

    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() throws Exception {
        Spark.stop();
        //server should be also stopped, because Spark works in Servlet Mode
        server.stop();

        /* wait until jetty completely releases port */
        while (!SparkTestUtil.isAvailable(PORT)) {
            SparkTestUtil.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }

    public static void setup(WebAppContext bb) {
        testUtil = new SparkTestUtil(PORT);

        ServerConnector connector = new ServerConnector(server);

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(PORT);
        server.setConnectors(new Connector[]{connector});

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
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("HEAD", SOMEPATH + "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetHiAfterFilter() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
            Assert.assertTrue(response.headers.get("after").contains("foobar"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello Root!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam1() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/shizzy", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: shizzy", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam2() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/gunit", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: gunit", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnauthorized() throws Exception {
        try {
            SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod("GET", SOMEPATH + "/protected/resource", null);
            Assert.assertTrue(urlResponse.status == 401);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNotFound() throws Exception {
        try {
            SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod("GET", SOMEPATH + "/no/resource", null);
            Assert.assertTrue(urlResponse.status == 404);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPost() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", SOMEPATH + "/poster", "Fo shizzy");
            Assert.assertEquals(201, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticResource() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/css/style.css", null);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("Content of css file"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticWelcomeResource() {
        try {
            SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/pages/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("<html><body>Hello Static World!</body></html>"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }
}
