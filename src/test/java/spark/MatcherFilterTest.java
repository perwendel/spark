package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import java.io.IOException;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.stop;

public class MatcherFilterTest {
    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        get("/juststatus", (q, a) -> {
            a.status(204);
            return null;
        });

        get("/justerror", (q, a) -> {
            throw new Exception("Failed method");
        });

        before("/justerrorfilter", (q, a) -> {
            throw new Exception("Failed filter");
        });

        exception(Exception.class, (exception, request, response) -> {
            response.status(400);
        });

        awaitInitialization();
    }

    @Test
    public void testJustStatus() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/juststatus", null);

        Assert.assertEquals(204, response.status);
    }

    @Test
    public void testException() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/justerror", null);

        Assert.assertEquals(400, response.status);
    }

    @Test
    public void testJustFilterException() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/justerrorfilter", null);

        Assert.assertEquals(404, response.status);
    }
}
