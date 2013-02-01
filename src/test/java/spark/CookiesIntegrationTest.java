package spark;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static spark.Spark.*;

/**
 * System tests for the Cookies support.
 * @author dreambrother
 */
public class CookiesIntegrationTest {

    private static final String DEFAULT_HOST_URL = "http://localhost:4567";
    private HttpClient httpClient = new DefaultHttpClient();
    
    @BeforeClass
    public static void initRoutes() throws InterruptedException {
        post(new Route("/assertNoCookies") {

            @Override
            public Object handle(Request request, Response response) {
                if (!request.cookies().isEmpty()) {
                    halt(500);
                }
                return "";
            }
        });
        
        post(new Route("/setCookie") {

            @Override
            public Object handle(Request request, Response response) {
                response.addCookie(request.queryParams("cookieName"), request.queryParams("cookieValue"));
                return "";
            }
        });
        
        post(new Route("/assertHasCookie") {

            @Override
            public Object handle(Request request, Response response) {
                String cookieValue = request.cookie(request.queryParams("cookieName"));
                if (!request.queryParams("cookieValue").equals(cookieValue)) {
                    halt(500);
                }
                return "";
            }
        });
        
        post(new Route("/removeCookie") {

            @Override
            public Object handle(Request request, Response response) {
                String cookieName = request.queryParams("cookieName");
                String cookieValue = request.cookie(cookieName);
                if (!request.queryParams("cookieValue").equals(cookieValue)) {
                    halt(500);
                }
                response.removeCookie(cookieName);
                return "";
            }
        });
    }
    
    @AfterClass
    public static void stopServer() {
        Spark.clearRoutes();
        Spark.stop();
    }
    
    @Test
    public void testEmptyCookies() {
        httpPost("/assertNoCookies");
    }
    
    @Test
    public void testCreateCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/assertHasCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
    }
    
    @Test
    public void testRemoveCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/removeCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost("/assertNoCookies");
    }
    
    private void httpPost(String relativePath) {
        HttpPost request = new HttpPost(DEFAULT_HOST_URL + relativePath);
        try {
            HttpResponse response = httpClient.execute(request);
            assertEquals(200, response.getStatusLine().getStatusCode());
        } catch (Exception ex) {
            fail(ex.toString());
        } finally {
            request.releaseConnection();
        }
    }
}
