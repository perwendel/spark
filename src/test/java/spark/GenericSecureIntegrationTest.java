package spark;

import static java.lang.Thread.sleep;
import static spark.SparkJ8.*;
import static spark.util.SparkTestUtil.getKeyStoreLocation;
import static spark.util.SparkTestUtil.getKeystorePassword;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class GenericSecureIntegrationTest {

    static SparkTestUtil testUtil;

    @AfterClass public static void tearDown () {
        stop ();
    }

    @BeforeClass public static void setup () throws InterruptedException {
        testUtil = new SparkTestUtil (4567);

        // note that the keystore stuff is retrieved from SparkTestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        Spark.setSecure (getKeyStoreLocation (), getKeystorePassword (), null, null);

        setupJ7 ();
        setupJ8 ();

        sleep (500);
    }

    private static void setupJ7 () {
        before (new Filter ("/protected/*") {
            @Override public void handle (Request request, Response response) {
                halt (401, "Go Away!");
            }
        });

        get (new Route ("/hi") {
            @Override public Object handle (Request request, Response response) {
                return "Hello World!";
            }
        });

        get (new Route ("/:param") {
            @Override public Object handle (Request request, Response response) {
                return "echo: " + request.params (":param");
            }
        });

        get (new Route ("/paramwithmaj/:paramWithMaj") {
            @Override public Object handle (Request request, Response response) {
                return "echo: " + request.params (":paramWithMaj");
            }
        });

        get (new Route ("/") {
            @Override public Object handle (Request request, Response response) {
                return "Hello Root!";
            }
        });

        post (new Route ("/poster") {
            @Override public Object handle (Request request, Response response) {
                String body = request.body ();
                response.status (201); // created
                return "Body was: " + body;
            }
        });

        patch (new Route ("/patcher") {
            @Override public Object handle (Request request, Response response) {
                String body = request.body ();
                response.status (200);
                return "Body was: " + body;
            }
        });

        after (new Filter ("/hi") {
            @Override public void handle (Request request, Response response) {
                response.header ("after", "foobar");
            }
        });
    }

    private static void setupJ8 () {
        before ("/j8/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/j8/hi", it -> "Hello World!");

        get ("/j8/:param", it -> "echo: " + it.params (":param"));

        get ("/j8/paramwithmaj/:paramWithMaj", it -> "echo: " + it.params (":paramWithMaj"));

        get ("/j8/", it -> "Hello Root!");

        post ("/j8/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        patch ("/j8/patcher", it -> {
            String body = it.requestBody ();
            it.status (200);
            return "Body was: " + body;
        });

        after ("/j8/hi", it -> it.header ("after", "foobar"));
    }

    @Test public void testGetHi () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("Hello World!", response.body);
    }

    @Test public void testHiHead () {
        UrlResponse response = testUtil.doMethodSecure ("HEAD", "/hi");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("", response.body);
    }

    @Test public void testGetHiAfterFilter () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/hi");
        Assert.assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void testGetRoot () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("Hello Root!", response.body);
    }

    @Test public void testEchoParam1 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/shizzy");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: shizzy", response.body);
    }

    @Test public void testEchoParam2 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/gunit");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: gunit", response.body);
    }

    @Test public void testEchoParamWithMaj () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/paramwithmaj/plop");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: plop", response.body);
    }

    @Test public void testUnauthorized () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/protected/resource");
        Assert.assertTrue (urlResponse.status == 401);
    }

    @Test public void testNotFound () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/no/resource");
        Assert.assertTrue (urlResponse.status == 404);
    }

    @Test public void testPost () {
        UrlResponse response = testUtil.doMethodSecure ("POST", "/poster", "Fo shizzy");
        System.out.println (response.body);
        Assert.assertEquals (201, response.status);
        Assert.assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void testPatch () {
        UrlResponse response = testUtil.doMethodSecure ("PATCH", "/patcher", "Fo shizzy");
        System.out.println (response.body);
        Assert.assertEquals (200, response.status);
        Assert.assertTrue (response.body.contains ("Fo shizzy"));
    }

    /*
     * Java 8 Lambda Tests
     */

    @Test public void testGetHiJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/hi");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("Hello World!", response.body);
    }

    @Test public void testHiHeadJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("HEAD", "/j8/hi");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("", response.body);
    }

    @Test public void testGetHiAfterFilterJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/hi");
        Assert.assertTrue (response.headers.get ("after").contains ("foobar"));
    }

    @Test public void testGetRootJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("Hello Root!", response.body);
    }

    @Test public void testEchoParam1J8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/shizzy");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: shizzy", response.body);
    }

    @Test public void testEchoParam2J8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/gunit");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: gunit", response.body);
    }

    @Test public void testEchoParamWithMajJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/paramwithmaj/plop");
        Assert.assertEquals (200, response.status);
        Assert.assertEquals ("echo: plop", response.body);
    }

    @Test public void testUnauthorizedJ8 () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/j8/protected/resource");
        Assert.assertTrue (urlResponse.status == 401);
    }

    @Test public void testNotFoundJ8 () throws Exception {
        UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/j8/no/resource");
        Assert.assertTrue (urlResponse.status == 404);
    }

    @Test public void testPostJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("POST", "/j8/poster", "Fo shizzy");
        System.out.println (response.body);
        Assert.assertEquals (201, response.status);
        Assert.assertTrue (response.body.contains ("Fo shizzy"));
    }

    @Test public void testPatchJ8 () {
        UrlResponse response = testUtil.doMethodSecure ("PATCH", "/j8/patcher", "Fo shizzy");
        System.out.println (response.body);
        Assert.assertEquals (200, response.status);
        Assert.assertTrue (response.body.contains ("Fo shizzy"));
    }
}
