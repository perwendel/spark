package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static spark.Spark.get;

public class UnsupportedMethodTest {
    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);
        get("/hi", (request, response) -> "Hello World!");
        try {
            Thread.sleep(500);
        } catch (Exception e) { }
    }

    @Test
    public void testNotFoundWhenUsingUnsupportedHttpMethod() throws Exception {
        UrlResponse response = testUtil.doMethod("LOCK", "/hi", null);
        assertThat(response.status, is(404));
    }
}
