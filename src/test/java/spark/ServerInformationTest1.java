package spark;

import org.junit.*;

import java.io.IOException;

import static spark.Spark.*;

public class ServerInformationTest1 {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8080;

    @BeforeClass
    public static void setup() throws IOException {
        ipAddress(IP_ADDRESS);
        port(PORT);
        secure("keystore.jks", null, null, null, true);
        init();
    }

    @AfterClass
    public static void tearDown() {
        stop();
    }

    @Test
    public void testIP() {
        String ipAddress = serverIP();
        Assert.assertEquals(IP_ADDRESS, ipAddress);
    }

    @Test
    public void testPort() {
        int port = serverPort();
        Assert.assertEquals(PORT, port);
    }

    @Test
    public void testCert() {
        boolean cert = serverNeedClientCert();
        Assert.assertTrue(cert);
    }
}
