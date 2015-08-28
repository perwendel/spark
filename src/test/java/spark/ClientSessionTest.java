package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.session.SessionType;
import spark.util.SparkTestUtil;

import javax.crypto.NoSuchPaddingException;
import java.security.*;

import static spark.Spark.after;
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
    public static void setup() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair encryptionKeyPair = keyPairGenerator.generateKeyPair();

        KeyPairGenerator dsa = KeyPairGenerator.getInstance("DSA");
        KeyPair signingKeyPair = dsa.generateKeyPair();

        setSessionStrategy(SessionType.Cookie, encryptionKeyPair, signingKeyPair);

        testUtil = new SparkTestUtil(4567);

        final String key = "TestKey";
        final String value = "#äüöß0♥²©";

        final String key2 = "TestKey2";
        final String value2 = "dkfj♥²©diel";

        after("/setSessionVariable", (request, response) -> request.session().attribute(key2, value2));

        get("/setSessionVariable", (request, response) -> {
            request.session().attribute(key, value);
            return "Successfully set session variable";
        });

        get("/checkSessionVariable", ((request, response) -> {
            String storedValue = request.session().attribute(key);
            if (!value.equals(storedValue)) {
                response.status(500);
                return "checkSessionVariable not ok. Expected: " + value + " got: " + storedValue + ".";
            }
            storedValue = request.session().attribute(key2);
            if (!value2.equals(storedValue)) {
                response.status(500);
                return "checkSessionVariable not ok. Expected: " + value2 + " got: " + storedValue + ".";
            }

            request.session().removeAttribute(key);
            response.status(200);
            return "checkSessionVariable ok";
        }));

        get("/checkNoSessionVariable", (((request, response) -> {
            Object attribute = request.session().attribute(key);
            if (attribute == null) {
                return "checkNoSessionVariable ok";
            }
            response.status(500);
            return "checkNoSessionVariableNotOk";
        })));

        final String key3 = "TestKey3";
        final String value3 = "dkfjsie9";
        get("/checkRedirect", (request, response) -> {
            request.session().attribute(key3, value3);
            response.redirect("/checkAfterRedirect");
            return null;
        });

        get("/checkAfterRedirect", (request, response) -> {
            Object attribute = request.session().attribute(key3);
            if (!value3.equals(attribute)) {
                response.status(500);
                return attribute;
            }
            return "OK";
        });

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
        SparkTestUtil.UrlResponse redirectResult = testUtil.doMethod("GET", "/checkRedirect", null);

        Assert.assertEquals(urlResponse.body, 200, urlResponse.status);
        Assert.assertEquals(urlResponse1.body, 200, urlResponse1.status);
        Assert.assertEquals(urlResponse2.body, 200, urlResponse2.status);
        Assert.assertEquals(redirectResult.body, 200, redirectResult.status);
    }
}
