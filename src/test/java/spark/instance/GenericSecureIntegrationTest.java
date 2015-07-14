package spark.instance;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.SparkAPI;
import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;


public class GenericSecureIntegrationTest {

    static SparkTestUtil testUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSecureIntegrationTest.class);

    private static SparkAPI spark;

    @AfterClass
    public static void tearDown() {
        spark.stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    @BeforeClass
    public static void setup() {
        spark = new SparkAPI();
        spark.port(4571);

        testUtil = new SparkTestUtil(4571);

        // note that the keystore stuff is retrieved from SparkTestUtil which
        // respects JVM params for keystore, password
        // but offers a default included store if not.
        spark.secure(SparkTestUtil.getKeyStoreLocation(),
                     SparkTestUtil.getKeystorePassword(), null, null);

        spark.before("/protected/*", (request, response) -> {
            spark.halt(401, "Go Away!");
        });

        spark.get("/hi", (request, response) -> {
            return "Hello World!";
        });

        spark.get("/:param", (request, response) -> {
            return "echo: " + request.params(":param");
        });

        spark.get("/paramwithmaj/:paramWithMaj", (request, response) -> {
            return "echo: " + request.params(":paramWithMaj");
        });

        spark.get("/", (request, response) -> {
            return "Hello Root!";
        });

        spark.post("/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        spark.patch("/patcher", (request, response) -> {
            String body = request.body();
            response.status(200);
            return "Body was: " + body;
        });

        spark.after("/hi", (request, response) -> {
            response.header("after", "foobar");
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetHi() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET",
                                                                         "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() {
        try {
            UrlResponse response = testUtil.doMethodSecure("HEAD", "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetHiAfterFilter() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET", "/hi", null);
            Assert.assertTrue(response.headers.get("after").contains("foobar"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetRoot() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET", "/", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello Root!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam1() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET", "/shizzy",
                                                           null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: shizzy", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParam2() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET", "/gunit",
                                                           null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: gunit", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEchoParamWithMaj() {
        try {
            UrlResponse response = testUtil.doMethodSecure("GET",
                                                           "/paramwithmaj/plop", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("echo: plop", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUnauthorized() throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethodSecure("GET", "/protected/resource", null);
            Assert.assertTrue(urlResponse.status == 401);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNotFound() throws Exception {
        try {
            UrlResponse urlResponse = testUtil.doMethodSecure("GET", "/no/resource", null);
            Assert.assertTrue(urlResponse.status == 404);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPost() {
        try {
            UrlResponse response = testUtil.doMethodSecure("POST", "/poster",
                                                           "Fo shizzy");
            LOGGER.info(response.body);
            Assert.assertEquals(201, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPatch() {
        try {
            UrlResponse response = testUtil.doMethodSecure("PATCH", "/patcher",
                                                           "Fo shizzy");
            LOGGER.info(response.body);
            Assert.assertEquals(200, response.status);
            Assert.assertTrue(response.body.contains("Fo shizzy"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
