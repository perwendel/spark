package spark;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static spark.Spark.*;

public class GenericIntegrationTest {

    static SparkTestUtil testUtil;
    
    @AfterClass
    public static void tearDown() {
        Spark.clearRoutes();
        Spark.stop();
    }
    
    @BeforeClass
    public static void setup() {
        testUtil = new SparkTestUtil(4567);

        staticFileRoute("/public");
        
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
    public void testEchoParamWithMaj() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/paramwithmaj/plop", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: plop", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = IOException.class)
    public void testUnauthorized() throws Exception {
        try {
            testUtil.doMethod("GET", "/protected/resource", null);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("401"));
            throw e;
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testNotFound() throws Exception {
        try {
            testUtil.doMethod("GET", "/no/resource", null);
        } catch (Exception e) {
            throw e;
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
    public void testStaticFile() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/static.html", null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of html file", response.body);
    }

}
