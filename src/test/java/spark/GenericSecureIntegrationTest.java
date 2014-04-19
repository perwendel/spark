package spark;

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

    @BeforeClass public static void setup () {
        testUtil = new SparkTestUtil (4567);

        // note that the keystore stuff is retrieved from SparkTestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        Spark.setSecure (getKeyStoreLocation (), getKeystorePassword (), null, null);

        setupJ7 ();
        setupJ8 ();

        try {
            Thread.sleep (500);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
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

        get ("/j8/paramwithmaj/:paramWithMaj", it ->
                "echo: " + it.params (":paramWithMaj")
        );

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
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/hi", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("Hello World!", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testHiHead () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("HEAD", "/hi", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testGetHiAfterFilter () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/hi", null);
            Assert.assertTrue (response.headers.get ("after").contains ("foobar"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testGetRoot () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("Hello Root!", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParam1 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/shizzy", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: shizzy", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParam2 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/gunit", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: gunit", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParamWithMaj () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/paramwithmaj/plop", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: plop", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testUnauthorized () throws Exception {
        try {
            UrlResponse urlResponse =
                testUtil.doMethodSecure ("GET", "/protected/resource", null);
            Assert.assertTrue (urlResponse.status == 401);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testNotFound () throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/no/resource", null);
            Assert.assertTrue (urlResponse.status == 404);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testPost () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("POST", "/poster", "Fo shizzy");
            System.out.println (response.body);
            Assert.assertEquals (201, response.status);
            Assert.assertTrue (response.body.contains ("Fo shizzy"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testPatch () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("PATCH", "/patcher", "Fo shizzy");
            System.out.println (response.body);
            Assert.assertEquals (200, response.status);
            Assert.assertTrue (response.body.contains ("Fo shizzy"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    /*
     * Java 8 Lambda Tests
     */

    @Test public void testGetHiJ8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/hi", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("Hello World!", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testHiHeadJ8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("HEAD", "/j8/hi", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testGetHiAfterFilterJ8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/hi", null);
            Assert.assertTrue (response.headers.get ("after").contains ("foobar"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testGetRootJ8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("Hello Root!", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParam1J8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/shizzy", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: shizzy", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParam2J8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("GET", "/j8/gunit", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: gunit", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testEchoParamWithMajJ8 () {
        try {
            UrlResponse response =
                testUtil.doMethodSecure ("GET", "/j8/paramwithmaj/plop", null);
            Assert.assertEquals (200, response.status);
            Assert.assertEquals ("echo: plop", response.body);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testUnauthorizedJ8 () throws Exception {
        try {
            UrlResponse urlResponse =
                testUtil.doMethodSecure ("GET", "/j8/protected/resource", null);
            Assert.assertTrue (urlResponse.status == 401);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testNotFoundJ8 () throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethodSecure ("GET", "/j8/no/resource", null);
            Assert.assertTrue (urlResponse.status == 404);
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testPostJ8 () {
        try {
            UrlResponse response = testUtil.doMethodSecure ("POST", "/j8/poster", "Fo shizzy");
            System.out.println (response.body);
            Assert.assertEquals (201, response.status);
            Assert.assertTrue (response.body.contains ("Fo shizzy"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }

    @Test public void testPatchJ8 () {
        try {
            UrlResponse response =
                testUtil.doMethodSecure ("PATCH", "/j8/patcher", "Fo shizzy");
            System.out.println (response.body);
            Assert.assertEquals (200, response.status);
            Assert.assertTrue (response.body.contains ("Fo shizzy"));
        }
        catch (Throwable e) {
            throw new RuntimeException (e);
        }
    }
}
