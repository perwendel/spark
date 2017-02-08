package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import static spark.Service.ignite;

/**
 * Created by Tom on 08/02/2017.
 */
public class ServicePortIntegrationTest {

    private static Service service;

    @BeforeClass
    public static void setUpClass() throws Exception {
        service = ignite();
        service.port(0);

        service.get("/hi", (q, a) -> "Hello World!");

        service.awaitInitialization();
    }

    @Test
    public void testGetPort_withRandomPort() throws Exception {
        int actualPort = service.port();
        SparkTestUtil testUtil = new SparkTestUtil(actualPort);

        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello World!", response.body);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }
}