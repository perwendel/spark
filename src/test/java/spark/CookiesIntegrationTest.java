package spark;

import static java.lang.Thread.sleep;
import static spark.SparkJ8.post;
import static spark.SparkJ8.stop;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import spark.util.SparkTestUtil;

/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
@Ignore
public class CookiesIntegrationTest {

    static SparkTestUtil testUtil;

    public static void initRoutesJ7 () throws InterruptedException {
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

    public static void initRoutesJ8 () throws InterruptedException {
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

    @BeforeClass public static void initRoutes () throws InterruptedException {
        testUtil = new SparkTestUtil (4567);

        initRoutesJ7 ();
        initRoutesJ8 ();

        sleep (500);
    }

    @AfterClass public static void stopServer () {
        stop ();
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

    private void httpPost (String aPath) {
        try {
            testUtil.doMethod ("GET", "http://localhost:4567" + aPath);
        }
        catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
