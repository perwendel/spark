package spark.instance_api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;
import spark.ModelAndView;
import spark.SparkInstance;
import spark.TemplateEngine;
import spark.examples.exception.BaseException;
import spark.examples.exception.NotFoundException;
import spark.examples.exception.SubclassOfBaseException;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class GenericIntegrationTest {

    private static final String NOT_FOUND_BRO = "Not found bro";
    static SparkTestUtil testUtil;
    static File tmpExternalFile;
    static SparkInstance spark;

    @AfterClass
    public static void tearDown() {
        spark.stop();
        if (tmpExternalFile != null) {
            tmpExternalFile.delete();
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        spark = SparkInstance.builder()
                .port(4567)
                .staticFilesAt("/public")
                .externalStaticFilesAt(System.getProperty("java.io.tmpdir"))
                .build();
        testUtil = new SparkTestUtil(4567);

        tmpExternalFile = new File(System.getProperty("java.io.tmpdir"), "externalFile.html");

        FileWriter writer = new FileWriter(tmpExternalFile);
        writer.write("Content of external file");
        writer.flush();
        writer.close();

        spark.before("/secretcontent/*", (request, response) -> {
            spark.halt(401, "Go Away!");
        });

        spark.before("/protected/*", "application/xml", (request, response) -> {
            spark.halt(401, "Go Away!");
        });

        spark.before("/protected/*", "application/json", (request, response) -> {
            spark.halt(401, "{\"message\": \"Go Away!\"}");
        });

        spark.get("/hi", "application/json", (request, response) -> {
            return "{\"message\": \"Hello World\"}";
        });

        spark.get("/hi", (request, response) -> {
            return "Hello World!";
        });

        spark.get("/param/:param", (request, response) -> {
            return "echo: " + request.params(":param");
        });

        spark.get("/paramandwild/:param/stuff/*", (request, response) -> {
            return "paramandwild: " + request.params(":param") + request.splat()[0];
        });

        spark.get("/paramwithmaj/:paramWithMaj", (request, response) -> {
            return "echo: " + request.params(":paramWithMaj");
        });

        spark.get("/templateView", (request, response) -> {
            return new ModelAndView("Hello", "my view");
        }, new TemplateEngine() {
            public String render(ModelAndView modelAndView) {
                return modelAndView.getModel() + " from " + modelAndView.getViewName();
            }
        });

        spark.get("/", (request, response) -> {
            return "Hello Root!";
        });

        spark.post("/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        spark.patch("/patcher", (request, response) -> {
            String body = request.body();
            response.status(200);
            return "Body was: " + body;
        });

        spark.after("/hi", (request, response) -> {
            response.header("after", "foobar");
        });

        spark.get("/throwexception", (request, response) -> {
            throw new UnsupportedOperationException();
        });

        spark.get("/throwsubclassofbaseexception", (request, response) -> {
            throw new SubclassOfBaseException();
        });

        spark.get("/thrownotfound", (request, response) -> {
            throw new NotFoundException();
        });

        spark.exception(UnsupportedOperationException.class, (exception, request, response) -> {
            response.body("Exception handled");
        });

        spark.exception(BaseException.class, (exception, request, response) -> {
            response.body("Exception handled");
        });

        spark.exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
            response.body(NOT_FOUND_BRO);
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    private static void registerEchoRoute(final String routePart) {
        spark.get("/tworoutes/" + routePart + "/:param", (request, response) -> {
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
