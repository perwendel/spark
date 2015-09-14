package spark;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import static org.junit.Assert.assertEquals;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;

/**
 * @author Tradunsky V.V.
 */
public class RouteHandleIntegrationTest {
    private static final String THROW_AN_EXCEPTION_ROUTE = "/throw";
    private static final String EXCEPTION_BODY_MESSAGE = "It's bad idea";
    private static final String GOOD_ROUTE = "/good";
    private static final String GOOD_BODY_MESSAGE = "Something good";
    public static final String UNKNOWN_REOUTE = "unknown";

    private static SparkTestUtil testUtil;

    @BeforeClass
    public static void initRoutes() throws InterruptedException {
        testUtil = new SparkTestUtil(4567);
        exception(IllegalArgumentException.class,
                (exception, request, response) -> {
                    response.status(Response.SC_BAD_REQUEST);
                    response.body(EXCEPTION_BODY_MESSAGE);
                });
        get(THROW_AN_EXCEPTION_ROUTE, (request, response) -> {
            throw new IllegalArgumentException();
        });

        get(GOOD_ROUTE, (request, response) -> GOOD_BODY_MESSAGE);

        delete(GOOD_ROUTE, (request, response) -> {
            response.status(Response.SC_NO_CONTENT);
            return null;
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void shouldHandleAnException() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod(HttpMethod.GET.asString(), THROW_AN_EXCEPTION_ROUTE, null);
        assertEquals("Should handle a status of response", Response.SC_BAD_REQUEST, response.status);
        assertEquals("Should handle a body of response", EXCEPTION_BODY_MESSAGE, response.body);
    }

    @Test
    public void shouldHandleARoute() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod(HttpMethod.GET.asString(), GOOD_ROUTE, null);
        assertEquals("Should handle a status of response", Response.SC_OK, response.status);
        assertEquals("Should handle a body of response", GOOD_BODY_MESSAGE, response.body);
    }

    @Test
    public void shouldSetAsNotFoundForUnhandledRoute() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod(HttpMethod.GET.asString(), UNKNOWN_REOUTE, null);
        assertEquals("Should handle a status of response", Response.SC_NOT_FOUND, response.status);
    }

    @Test
    public void shouldSuccessProcessedWithNoContent() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod(HttpMethod.DELETE.asString(), GOOD_ROUTE, null);
        assertEquals("Should handle a status of response", Response.SC_NO_CONTENT, response.status);
    }
}
