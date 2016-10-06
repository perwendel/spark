package spark;

import org.junit.*;
import spark.http.matching.RedispatchRequestWrapper;
import spark.route.HttpMethod;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

import static org.junit.Assert.*;

public class RedispatchTest {

    private static SparkTestUtil testUtil;
    private static RedispatchRequestWrapper cachedRequest;

    @BeforeClass
    public static void setup() {
        testUtil = new SparkTestUtil(4567);

        //REDISPATCHERS
        Spark.get("/redispatch/get", (req, res) -> {
            return Spark.redispatch("/redispatched", req, res);
        });

        Spark.get("/redispatch/post", (req, res) -> {
            return Spark.redispatch("/redispatched", req, res, HttpMethod.post);
        });

        Spark.get("/redispatch/get/withParam", (req, res) -> {
            return Spark.redispatch("/redispatched/foo?" + req.queryString(), req, res);
        });

        Spark.get("/filters/redispatch", (req, res) -> {
            return Spark.redispatch("/filters/redispatched", req, res);
        });

        //ROUTES
        Spark.post("/redispatched", (req, res) -> {
            cachedRequest = ((RedispatchRequestWrapper) req);
            res.status(265); //whatever
            return "POST";
        });

        Spark.get("/redispatched", (req, res) -> {
            cachedRequest = ((RedispatchRequestWrapper) req);
            res.status(269); //whatever
            return "GET";
        });

        Spark.get("/redispatched/:param", (req, res) -> {
            cachedRequest = ((RedispatchRequestWrapper) req);
            return "OK";
        });

        Spark.get("/filters/redispatched", (req, res) -> {
            cachedRequest = ((RedispatchRequestWrapper) req);
            res.header("redispatch", "1");
            return "OK";
        });

        Spark.before("/filters/redispatched", (req, res) -> res.header("before1", "b1"));
        Spark.before("/filters/redispatched", (req, res) -> res.header("before2", "b2"));
        Spark.after("/filters/redispatched", (req, res) -> res.header("after1", "a1"));

        Spark.awaitInitialization();
    }

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @Before
    public void clearVars() {
        cachedRequest = null;
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
        assertEquals("duck=qwak!", cachedRequest.queryString());
        assertEquals("qwak!", cachedRequest.queryMap("duck").value());
        assertEquals("foo", cachedRequest.params(":param"));
        assertEquals(1, cachedRequest.params().size());
    }

    @Test
    public void redispatchedRequestShouldGoThroughFilters() throws Exception {
        UrlResponse response = testUtil.get("/filters/redispatch");
        assertEquals("OK", response.body);
        assertEquals("1", response.headers.get("redispatch"));
        assertEquals("b1", response.headers.get("before1"));
        assertEquals("b2", response.headers.get("before2"));
        assertEquals("a1", response.headers.get("after1"));
    }
}
