package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.session.SessionType;
import spark.util.SparkTestUtil;

import static spark.Spark.get;
import static spark.SparkBase.setSessionStrategy;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public class ClientSessionTest {
    static SparkTestUtil testUtil;

    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    @BeforeClass
    public static void setup() {
        setSessionStrategy(SessionType.Cookie);

        testUtil = new SparkTestUtil(4567);

        final String key = "TestKey";
        final String value = "#äüöß0♥²©";

        get("/setSessionVariable", (request, response) -> {
            request.session().attribute(key, value);
            return "Successfully set session variable";
        });

        get("/checkSessionVariable", ((request, response) -> {
            String storedValue = request.session().attribute(key);
            if (value.equals(storedValue)) {
                request.session().removeAttribute(key);
                response.status(200);
                return "checkSessionVariable ok";
            }
            response.status(500);
            return "checkSessionVariable not ok. Expected: " + value + " got: " + storedValue + ".";
        }));

        get("/checkNoSessionVariable", (((request, response) -> {
            Object attribute = request.session().attribute(key);
            if (attribute == null) {
                return "checkNoSessionVariable ok";
            }
            response.status(500);
            return "checkNoSessionVariableNotOk";
        })));

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

    @Test
    public void testSessionVariables() throws Exception {
        SparkTestUtil.UrlResponse urlResponse = testUtil.doMethod("GET", "/setSessionVariable", null);
        SparkTestUtil.UrlResponse urlResponse1 = testUtil.doMethod("GET", "/checkSessionVariable", null);
        SparkTestUtil.UrlResponse urlResponse2 = testUtil.doMethod("GET", "/checkNoSessionVariable", null);
        Assert.assertEquals(urlResponse.body, 200, urlResponse.status);
        Assert.assertEquals(urlResponse1.body, 200, urlResponse1.status);
        Assert.assertEquals(urlResponse2.body, 200, urlResponse2.status);
    }
}
