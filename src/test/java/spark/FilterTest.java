package spark;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static spark.Spark.before;

public class FilterTest {
    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        before("/justfilter", (q, a) -> System.out.println("Filter matched"));
    }

    @Test
    public void testJustFilter() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/justfilter", null);
        Assert.assertEquals(404, response.status);
    }

}
