package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static spark.Service.ignite;

public class InitExceptionHandlerTest {

    private static int NON_VALID_PORT = Integer.MAX_VALUE;
    private static Service service;
    private static String errorMessage = "";

    @BeforeClass
    public static void setUpClass() throws Exception {
        service = ignite();
        service.port(NON_VALID_PORT);
        service.initExceptionHandler((e) -> errorMessage = "Custom init error");
        service.init();
        service.awaitInitialization();
    }

    @Test
    public void testInitExceptionHandler() throws Exception {
        Assert.assertEquals("Custom init error", errorMessage);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service.stop();
    }

}
