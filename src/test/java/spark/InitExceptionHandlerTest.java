package spark;

import static spark.Service.ignite;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InitExceptionHandlerTest {

    private static Service service1;
    private static Service service2;
    private static String errorMessage = "";


    @BeforeClass
    public static void setUpClass() throws Exception {
        service1 = ignite();
        service1.port(1122);
        service1.init();
        service1.awaitInitialization();

        service2 = ignite();
        service2.port(70000);
        service2.initExceptionHandler((e) -> errorMessage = "Custom init error");
        service2.init();
        service2.awaitInitialization();
    }

    @Test
    public void testGetPort_withRandomPort() throws Exception {
        Assert.assertEquals("Custom init error", errorMessage);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        service1.stop();
        service2.stop();
    }

}
