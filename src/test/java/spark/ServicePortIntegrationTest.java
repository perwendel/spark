package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.util.SparkTestUtil;

import static spark.Service.ignite;

/**
 * Created by Tom on 08/02/2017.
 */
public class ServicePortIntegrationTest {

    private static Service service;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePortIntegrationTest.class);

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

        LOGGER.info("got port ");

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