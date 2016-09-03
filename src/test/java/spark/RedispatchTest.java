package spark;

import org.junit.*;
import spark.http.matching.RedispatchRequestWrapper;
import spark.route.HttpMethod;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static org.junit.Assert.*;

public class RedispatchTest {

    static SparkTestUtil testUtil;
    static RedispatchRequestWrapper catchedRequest;

    @BeforeClass
    public static void setup() {
        testUtil = new SparkTestUtil(4567);

        //REDISPATCHERS
        Spark.get("/redispatch/get", (req, res) -> {
            return Spark.redispatch("/redispatched", req, res);
        });

        Spark.get("/redispatch/post", (req, res) -> {
            return Spark.redispatch("/redispatched", HttpMethod.post, req, res);
        });

        Spark.get("/redispatch/get/withParam", (req, res) -> {
            return Spark.redispatch("/redispatched/foo?" + req.queryString(), req, res);
        });

        //ROUTES
        Spark.post("/redispatched", (req, res) -> {
            catchedRequest = ((RedispatchRequestWrapper) req);
            res.status(265); //whatever
            return "POST";
        });

        Spark.get("/redispatched", (req, res) -> {
            catchedRequest = ((RedispatchRequestWrapper) req);
            res.status(269); //whatever
            return "GET";
        });

        Spark.get("/redispatched/:param", (req, res) -> {
            catchedRequest = ((RedispatchRequestWrapper) req);
            return "OK";
        });

        Spark.awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @After
    public void clearVars() {
        catchedRequest = null;
    }

    @Test
    public void redispatchedRequestShouldGoThroughGET() throws Exception {
        UrlResponse response = testUtil.get("/redispatch/get");
        assertEquals(269, response.status);
        assertEquals("GET", response.body);
    }

    @Test
    public void redispatchedRequestShouldGoThroughPOST() throws Exception {
        UrlResponse response = testUtil.get("/redispatch/post");
        assertEquals(265, response.status);
        assertEquals("POST", response.body);
    }

    @Test
    public void redispatchedRequestShouldHaveExpectedParamsAndQueries() throws Exception {
        UrlResponse response = testUtil.get("/redispatch/get/withParam?duck=qwak!");
        assertEquals("OK", response.body);
        assertEquals("duck=qwak!", catchedRequest.queryString());
        assertEquals("qwak!", catchedRequest.queryMap("duck").value());
        assertEquals("foo", catchedRequest.params(":param"));
        assertEquals(1, catchedRequest.params().size());
    }
}
