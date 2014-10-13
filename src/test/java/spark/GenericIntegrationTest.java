package spark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.examples.exception.BaseException;
import spark.examples.exception.NotFoundException;
import spark.examples.exception.SubclassOfBaseException;
import spark.servlet.ServletTest;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.patch;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

public class GenericIntegrationTest {

    private static final String NOT_FOUND_BRO = "Not found bro";

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericIntegrationTest.class);

    static SparkTestUtil testUtil;
    static File tmpExternalFile;

    @AfterClass
    public static void tearDown() {
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

        before("/secretcontent/*", (request, response) -> {
            halt(401, "Go Away!");
        });

        before("/protected/*", "application/xml", (request, response) -> {
            halt(401, "Go Away!");
        });

        before("/protected/*", "application/json", (request, response) -> {
            halt(401, "{\"message\": \"Go Away!\"}");
        });

        get("/hi", "application/json", (request, response) -> {
            return "{\"message\": \"Hello World\"}";
        });

        get("/hi", (request, response) -> {
            return "Hello World!";
        });

        get("/param/:param", (request, response) -> {
            return "echo: " + request.params(":param");
        });

        get("/paramandwild/:param/stuff/*", (request, response) -> {
            return "paramandwild: " + request.params(":param") + request.splat()[0];
        });

        get("/paramwithmaj/:paramWithMaj", (request, response) -> {
            return "echo: " + request.params(":paramWithMaj");
        });

        get("/templateView", (request, response) -> {
            return new ModelAndView("Hello", "my view");
        }, new TemplateEngine() {
            public String render(ModelAndView modelAndView) {
                return modelAndView.getModel() + " from " + modelAndView.getViewName();
            }
        });

        get("/", (request, response) -> {
            return "Hello Root!";
        });

        post("/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        patch("/patcher", (request, response) -> {
            String body = request.body();
            response.status(200);
            return "Body was: " + body;
        });

        after("/hi", (request, response) -> {
            response.header("after", "foobar");
        });

        get("/throwexception", (request, response) -> {
            throw new UnsupportedOperationException();
        });

        get("/throwsubclassofbaseexception", (request, response) -> {
            throw new SubclassOfBaseException();
        });

        get("/thrownotfound", (request, response) -> {
            throw new NotFoundException();
        });

        exception(UnsupportedOperationException.class, (exception, request, response) -> {
            response.body("Exception handled");
        });

        exception(BaseException.class, (exception, request, response) -> {
            response.body("Exception handled");
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
            response.body(NOT_FOUND_BRO);
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @Test
    public void filters_should_be_accept_type_aware() throws Exception {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/protected/resource", null, "application/json");
            Assert.assertTrue(response.status == 401);
            Assert.assertEquals("{\"message\": \"Go Away!\"}", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void routes_should_be_accept_type_aware() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/hi", null, "application/json");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("{\"message\": \"Hello World\"}", response.body);
    }

    @Test
    public void template_view_should_be_rendered_with_given_model_view_object() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/templateView", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Hello from my view", response.body);
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
        get("/tworoutes/" + routePart + "/:param", (request, response) -> {
            return routePart + " route: " + request.params(":param");
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
            UrlResponse response = testUtil.doMethod("GET", "/secretcontent/whateva", null);
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
            LOGGER.info(response.body);
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
            LOGGER.info(response.body);
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

    @Test
    public void testExceptionMapper() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/throwexception", null);
        Assert.assertEquals("Exception handled", response.body);
    }

    @Test
    public void testInheritanceExceptionMapper() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/throwsubclassofbaseexception", null);
        Assert.assertEquals("Exception handled", response.body);
    }

    @Test
    public void testNotFoundExceptionMapper() throws Exception {
        //        thrownotfound
        UrlResponse response = testUtil.doMethod("GET", "/thrownotfound", null);
        Assert.assertEquals(NOT_FOUND_BRO, response.body);
        Assert.assertEquals(404, response.status);
    }
}
