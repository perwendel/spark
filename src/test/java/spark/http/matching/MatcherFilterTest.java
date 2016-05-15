package spark.http.matching;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;
import spark.util.SparkTestUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Kevin on 5/14/2016.
 */
public class MatcherFilterTest {
    private static SparkTestUtil http;
    private static class Problem {
        public String create() throws Exception {
            throw new Exception();
        }
    }
    @BeforeClass
    public static void setup() {
        http = new SparkTestUtil(4567);
        Spark.get("/", ((request, response) -> "Home"));
        Spark.get("/error", ((request, response) -> new Problem().create()));
        Spark.awaitInitialization();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void testCustom404() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/kevin");
            assertEquals("Response status is 404", 404, response.status);
            assertTrue("Body has contents of custom HTML", response.body.contains(":-("));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCustom500() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/error");
            assertEquals("Response status is 404", 500, response.status);
            System.out.println(response.body);
            assertTrue("Body has contents of custom HTML", response.body.contains("Oops"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
