package spark;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static spark.Spark.*;

public class GenericIntegrationTest {

    static SparkTestUtil testUtil;
    static File tmpExternalFile;

    @AfterClass
    public static void tearDown() {
        Spark.clearRoutes();
        Spark.stop();
        if (tmpExternalFile != null) {
            tmpExternalFile.delete();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write("Content of external file");
        writer.flush();
        writer.close();

        staticFileLocation("/public");
        externalStaticFileLocation(System.getProperty("java.io.tmpdir"));

        before(new Filter("/protected/*") {

            @Override
            public void handle(Request request, Response response) {
                halt(401, "Go Away!");
            }
        });

        get(new Route("/hi") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });

        get(new Route("/param/:param") {

            @Override
            public Object handle(Request request, Response response) {
                return "echo: " + request.params(":param");
            }
        });
        
        get(new Route("/paramandwild/:param/stuff/*") {
            @Override
            public Object handle(Request request, Response response) {
                return "paramandwild: " + request.params(":param") + request.splat()[0];
            }
        });

        get(new Route("/paramwithmaj/:paramWithMaj") {

            @Override
            public Object handle(Request request, Response response) {
                return "echo: " + request.params(":paramWithMaj");
            }
        });

        get(new Route("/") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello Root!";
            }
        });

        post(new Route("/poster") {
            @Override
            public Object handle(Request request, Response response) {
                String body = request.body();
                response.status(201); // created
                return "Body was: " + body;
            }
        });

        patch(new Route("/patcher") {
            @Override
            public Object handle(Request request, Response response) {
                String body = request.body();
                response.status(200);
                return "Body was: " + body;
            }
        });

        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                response.header("after", "foobar");
            }
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetHi() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() {
        try {
            UrlResponse response = testUtil.doMethod("HEAD", "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetHiAfterFilter() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/hi", null);
            Assert.assertTrue(response.headers.get("after").contains("foobar"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello Root!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testParamAndWild() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/paramandwild/thedude/stuff/andits", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("paramandwild: thedudeandits", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testEchoParam1() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/param/shizzy", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: shizzy", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam2() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/param/gunit", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: gunit", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParamWithUpperCaseInValue() {
        final String camelCased = "ThisIsAValueAndSparkShouldRetainItsUpperCasedCharacters";
        try {
            UrlResponse response = testUtil.doMethod("GET", "/param/" + camelCased, null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: " + camelCased, response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTwoRoutesWithDifferentCaseButSameName() {
        String lowerCasedRoutePart = "param";
        String uppperCasedRoutePart = "PARAM";

        registerEchoRoute(lowerCasedRoutePart);
        registerEchoRoute(uppperCasedRoutePart);
        try {
            assertEchoRoute(lowerCasedRoutePart);
            assertEchoRoute(uppperCasedRoutePart);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerEchoRoute(final String routePart) {
        get(new Route("/tworoutes/" + routePart + "/:param") {
            @Override
            public Object handle(Request request, Response response) {
                return routePart + " route: " + request.params(":param");
            }
        });
    }

    private static void assertEchoRoute(String routePart) throws Exception {
        final String expected = "expected";
        UrlResponse response = testUtil.doMethod("GET", "/tworoutes/" + routePart + "/" + expected, null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals(routePart + " route: " + expected, response.body);
    }
    
    @Test
    public void testEchoParamWithMaj() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/paramwithmaj/plop", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: plop", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnauthorized() throws Exception {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/protected/resource", null);
            Assert.assertTrue(response.status == 401);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNotFound() throws Exception {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/no/resource", null);
            Assert.assertTrue(response.status == 404);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPost() {
        try {
            UrlResponse response = testUtil.doMethod("POST", "/poster", "Fo shizzy");
            System.out.println(response.body);
            Assert.assertEquals(201, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPatch() {
        try {
            UrlResponse response = testUtil.doMethod("PATCH", "/patcher", "Fo shizzy");
            System.out.println(response.body);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticFile() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/css/style.css", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of css file", response.body);
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/externalFile.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }

}
