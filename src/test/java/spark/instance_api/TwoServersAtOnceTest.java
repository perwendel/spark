package spark.instance_api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.Route;
import spark.Spark;
import spark.utils.IOUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TwoServersAtOnceTest {

    private static Spark.Api spark1;
    private static Spark.Api spark2;

    @BeforeClass
    public static void setUp() {
        spark1 = Spark.builder().port(4567).build();
        spark2 = Spark.builder().port(4568).build();

        String path = "/someResource";
        Route route = (request, response) -> "SOME RESOURCE";
        spark1.get(path, route);
        spark2.get(path, route);

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @AfterClass
    public static void tearDown() {
        spark1.stop();
        spark2.stop();
    }

    @Test
    public void test() {
        UrlResponse response1 = doMethod(4567, "GET", "/someResource", null);
        UrlResponse response2 = doMethod(4568, "GET", "/someResource", null);
        assertNotNull(response1);
        assertNotNull(response1.body);
        assertEquals(200, response1.status);
        assertTrue(response1.body.contains("SOME RESOURCE"));
        assertNotNull(response2);
        assertNotNull(response2.body);
        assertEquals(200, response2.status);
        assertTrue(response2.body.contains("SOME RESOURCE"));
    }

    private UrlResponse doMethod(int port, String requestMethod, String path, String body) {
        UrlResponse response = new UrlResponse();

        try {
            getResponse(port, requestMethod, path, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void getResponse(int port, String requestMethod, String path, UrlResponse response)
            throws IOException {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.connect();
        String res = IOUtils.toString(connection.getInputStream());
        response.body = res;
        response.status = connection.getResponseCode();
        response.headers = connection.getHeaderFields();
    }

    private static class UrlResponse {
        public Map<String, List<String>> headers;
        private String body;
        private int status;
    }
}
