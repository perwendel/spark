package spark.embeddedserver.jetty;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import spark.util.SparkTestUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static spark.Spark.*;

// A proxy server would change the Host header to x-forwarded-host when repeating the request to the target server.
public class JettyHandlerTest {

    public static final String HELLO = "/hello";

    public static final int PORT = 4567;

    public static final String HELLO_WORLD = "Hello World!";

    private static SparkTestUtil http;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        http = new SparkTestUtil(4567);

        get(HELLO, (q, a) -> {
            assertEquals("curl/7.55.1",q.userAgent());
            assertEquals("proxy.mydomain.com", q.host());
            return HELLO_WORLD;
        });

        post(HELLO, (q, a) -> {
            assertEquals("curl/7.55.1",q.userAgent());
            assertEquals("proxy.mydomain.com", q.host());
            return HELLO_WORLD;
        });

        Spark.awaitInitialization();
    }

    //CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/1069
    @Test
    public void testHELLOWithXForwardedHostGET() {
        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            requestHeader.put("x-forwarded-host", "proxy.mydomain.com");
            SparkTestUtil.UrlResponse response = http.doMethod("GET",HELLO, "", false, "*/*", requestHeader);

            assertEquals(200, response.status);
            assertEquals(HELLO_WORLD, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    //CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/1069
    @Test
    public void testHELLOWithXForwardedHostPOST() {
        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            requestHeader.put("x-forwarded-host", "proxy.mydomain.com");
            SparkTestUtil.UrlResponse response = http.doMethod("POST",HELLO, "", false, "*/*", requestHeader);

            assertEquals(200, response.status);
            assertEquals(HELLO_WORLD, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
