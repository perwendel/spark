package sparkj8c;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spark.SparkJ8C.post;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

/**
 * System tests for the Cookies support.
 * @author dreambrother
 */
public class CookiesIntegrationTest {

    private static final String DEFAULT_HOST_URL = "http://localhost:4567";
    private HttpClient httpClient = new DefaultHttpClient();

    @BeforeClass
    public static void initRoutes() throws InterruptedException {
        post("/assertNoCookies", it -> {
			if (!it.cookies().isEmpty()) {
				it.halt(500);
			}
			return "";
        });

        post("/setCookie", it -> {
			it.cookie(it.queryParams("cookieName"), it.queryParams("cookieValue"));
			return "";
        });

        post("/assertHasCookie", it -> {
			String cookieValue = it.cookie(it.queryParams("cookieName"));
			if (!it.queryParams("cookieValue").equals(cookieValue)) {
				it.halt(500);
			}
			return "";
        });

        post("/removeCookie", it -> {
			String cookieName = it.queryParams("cookieName");
			String cookieValue = it.cookie(cookieName);
			if (!it.queryParams("cookieValue").equals(cookieValue)) {
				it.halt(500);
			}
			it.removeCookie(cookieName);
			return "";
        });
    }

    @AfterClass
    public static void stopServer() {
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
