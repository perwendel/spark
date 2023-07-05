package spark;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static spark.Spark.*;

public class FilterTest {
    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        before("/justfilter", (q, a) -> System.out.println("Filter matched"));
        get("/foo", (req, resp) -> {
            Spark.halt(400, "Exception");
            return "";
        });
        afterAfter("/foo", (req, a) -> {
            Assert.assertEquals("Exception", a.body());
            System.out.println(req);
            System.out.println(a);
        });
        awaitInitialization();
    }

    @Test
    public void testJustFilter() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/justfilter", null);

        System.out.println("response.status = " + response.status);
        Assert.assertEquals(404, response.status);
    }

    @Test
    public void testAfterAfter() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/foo", null);

        System.out.println("response.status = " + response.status);
        Assert.assertEquals(400, response.status);
        Assert.assertEquals("Exception", response.body);
    }
}
