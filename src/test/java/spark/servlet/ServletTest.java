package spark.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.TAccess;
import testutil.MyTestUtil;
import testutil.MyTestUtil.UrlResponse;

public class ServletTest {

    private static final String SOMEPATH = "/somepath";
    private static final int PORT = 9393;
    static final Server server = new Server();
    
    static MyTestUtil testUtil;
    
    @AfterClass
    public static void tearDown() {
        TAccess.clearRoutes();
        TAccess.stop();
    }
    
    @BeforeClass
    public static void setup() throws InterruptedException {
		testUtil = new MyTestUtil(PORT);
		
		SocketConnector connector = new SocketConnector();
		
		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(PORT);
		server.setConnectors(new Connector[] { connector });
		
		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath(SOMEPATH);
		bb.setWar("src/test/webapp");
		
		server.setHandler(bb);
		
		final CountDownLatch latch = new CountDownLatch(1);
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(">>> STARTING EMBEDDED JETTY SERVER for jUnit testing of SparkFilter");
                    server.start();
                    latch.countDown();
                    System.in.read();
                    System.out.println(">>> STOPPING EMBEDDED JETTY SERVER"); 
                    server.stop();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(100);
                }        
            }
        }).start();
		
        // make sure server is started
		latch.await();
    }
    
    @Test
    public void testGetHi() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }
    
    @Test
    public void testHiHead() throws Exception {
        UrlResponse response = testUtil.doMethod("HEAD", SOMEPATH + "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("", response.body);
    }
    
    @Test
    public void testGetHiAfterFilter() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/hi", null);
        Assert.assertTrue(response.headers.get("after").contains("foobar"));
    }
    
    @Test
    public void testGetRoot() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello Root!", response.body);
    }
    
    @Test
    public void testEchoParam1() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/shizzy", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: shizzy", response.body);
    }

    @Test
    public void testEchoParam2() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", SOMEPATH + "/gunit", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("echo: gunit", response.body);
    }

    @Test(expected = IOException.class)
    public void testUnauthorized() throws Exception {
        try {
            testUtil.doMethod("GET", SOMEPATH + "/protected/resource", null);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("401"));
            throw e;
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testNotFound() throws Exception {
        testUtil.doMethod("GET", SOMEPATH + "/no/resource", null);
    }
    
    @Test
    public void testPost() throws Exception {
        UrlResponse response = testUtil.doMethod("POST", SOMEPATH + "/poster", "Fo shizzy");
        System.out.println(response.body);
        Assert.assertEquals(201, response.status);
        Assert.assertTrue(response.body.contains("Fo shizzy"));
    }
}
