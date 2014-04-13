package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spark.SparkJ8.post;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
public class CookiesIntegrationTest {

    private static final String DEFAULT_HOST_URL = "http://localhost:4567";
    private HttpClient httpClient = new DefaultHttpClient ();

    @BeforeClass public static void initRoutes () throws InterruptedException {
        post (new Route ("/assertNoCookies") {
            @Override public Object handle (Request request, Response response) {
                if (!request.cookies ().isEmpty ())
                    halt (500);
                return "";
            }
        });

        post (new Route ("/setCookie") {
            @Override public Object handle (Request request, Response response) {
                response.cookie (
                    request.queryParams ("cookieName"),
                    request.queryParams ("cookieValue"));
                return "";
            }
        });

        post (new Route ("/assertHasCookie") {
            @Override public Object handle (Request request, Response response) {
                String cookieValue = request.cookie (request.queryParams ("cookieName"));
                if (!request.queryParams ("cookieValue").equals (cookieValue))
                    halt (500);
                return "";
            }
        });

        post (new Route ("/removeCookie") {
            @Override public Object handle (Request request, Response response) {
                String cookieName = request.queryParams ("cookieName");
                String cookieValue = request.cookie (cookieName);
                if (!request.queryParams ("cookieValue").equals (cookieValue))
                    halt (500);
                response.removeCookie (cookieName);
                return "";
            }
        });
    }

    @BeforeClass public static void initRoutesJ8 () throws InterruptedException {
        post ("/j8/assertNoCookies", it -> {
            if (!it.cookies ().isEmpty ()) {
                it.halt (500);
            }
            return "";
        });

        post ("/j8/setCookie", it -> {
            it.cookie (it.queryParams ("cookieName"), it.queryParams ("cookieValue"));
            return "";
        });

        post ("/j8/assertHasCookie", it -> {
            String cookieValue = it.cookie (it.queryParams ("cookieName"));
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            return "";
        });

        post ("/j8/removeCookie", it -> {
            String cookieName = it.queryParams ("cookieName");
            String cookieValue = it.cookie (cookieName);
            if (!it.queryParams ("cookieValue").equals (cookieValue))
                it.halt (500);
            it.removeCookie (cookieName);
            return "";
        });
    }

    @AfterClass public static void stopServer () {
        Spark.stop ();
    }

    @Test public void emptyCookies () {
        httpPost ("/assertNoCookies");
    }

    @Test public void createCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost ("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost ("/assertHasCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
    }

    @Test public void removeCookie () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost ("/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost ("/removeCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost ("/assertNoCookies");
    }

    @Test public void emptyCookiesJ8 () {
        httpPost ("/j8/assertNoCookies");
    }

    @Test public void createCookieJ8 () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost ("/j8/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost (
            "/j8/assertHasCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
    }

    @Test public void removeCookieJ8 () {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost ("/j8/setCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost ("/j8/removeCookie?cookieName=" + cookieName + "&cookieValue=" + cookieValue);
        httpPost ("/j8/assertNoCookies");
    }

    private void httpPost (String relativePath) {
        HttpPost request = new HttpPost (DEFAULT_HOST_URL + relativePath);
        try {
            HttpResponse response = httpClient.execute (request);
            assertEquals (200, response.getStatusLine ().getStatusCode ());
        }
        catch (Exception ex) {
            fail (ex.toString ());
        }
        finally {
            request.releaseConnection ();
        }
    }
}
