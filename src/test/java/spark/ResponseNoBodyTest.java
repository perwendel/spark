package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static org.junit.Assert.*;

public class ResponseNoBodyTest {

    private static SparkTestUtil httpClient;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() {
        httpClient = new SparkTestUtil(4567);

        Spark.get("/nobody", (request, response) -> null);

        Spark.get("/nobody204", (request, response) -> {
            response.status(204);
            return null;
        });

        Spark.awaitInitialization();
    }

    @Test
    public void testResponse_whenNull_retrieveResponseWithoutBody() throws Exception {
        UrlResponse response = httpClient.get("/nobody");
        assertTrue(response.body.equals(""));
        assertTrue(response.status == 200);
    }

    @Test
    public void testResponse_whenNull_retrieveResponseWithoutBody204() throws Exception {
        UrlResponse response = httpClient.get("/nobody204");
        assertTrue(response.body.equals(""));
        assertTrue(response.status == 204);
    }
}
