package spark;

import static spark.Spark.*;

import java.io.PrintWriter;

import javax.servlet.AsyncContext;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;

/**
 * @author : Steve Hurlbut
 */
public class AsyncRequestIntegrationTest {
    private static SparkTestUtil http;


    @BeforeClass
    public static void setup() {
        http = new SparkTestUtil(4567);

        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(exception.getMessage());
        });

        before("/test", (req, res) -> {
            res.header("BeforeFilter", "true");
        });

        after("/test", (req, res) -> {
            res.header("AfterFilter", "true");
        });

        afterAfter("/test", (req, res) -> {
            res.header("AfterAfterFilter", "true");
        });

        // Spark style implementation of async requests
        get("/test", (req, res) -> {
            boolean isAsync = "true".equals(req.queryParams("async"));
            if (isAsync) {
                final AsyncContext ctx = req.startAsync();
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        res.body("Async!");
                        ctx.complete();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            }
            return "completed synchronously";
        });

        get("/test/slow", (req, res) -> {
            final AsyncContext ctx = req.startAsync();
            ctx.setTimeout(500L);
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    res.body("Async!");
                    ctx.complete();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            return "completed synchronously";
        });

        get("/test/error", (req, res) -> {
            final AsyncContext ctx = req.startAsync();
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(500);
                    res.status(500);
                    res.body("Something bad happened");
                    ctx.complete();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            return "completed synchronously";
        });

        // servlet style implementation of async requests
        get("/traditional", (req, res) -> {
            final AsyncContext ctx = req.raw().startAsync();
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(500);
                    PrintWriter writer = ctx.getResponse().getWriter();
                    writer.write("lots of data");
                    writer.close();
                    ctx.complete();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            return "completed synchronously";
        });

        awaitInitialization();
    }

    @AfterClass
    public static void stopServer() {
        stop();
    }

    // verify synchronous requests still work
    @Test
    public void testSync() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/test");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("completed synchronously", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // verify body, before, after, and afterAfter files are processed with async requests
    @Test
    public void testAsync() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/test?async=true");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Async!", response.body);
            Assert.assertEquals("true", response.headers.get("BeforeFilter"));
            Assert.assertEquals("true", response.headers.get("AfterFilter"));
            Assert.assertEquals("true", response.headers.get("AfterAfterFilter"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // verify timeouts are properly handled
    @Test
    public void testTimeout() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/test/slow");
            Assert.assertEquals(500, response.status);
            Assert.assertEquals("Asynchronous request timeout expired", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // verify error handling works properly
    @Test
    public void testError() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/test/error");
            Assert.assertEquals(500, response.status);
            Assert.assertTrue(response.body.contains("Something bad happened"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // verify traditional servlet style processing of async requests works
    @Test
    public void testTraditional() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/traditional");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("lots of data", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
