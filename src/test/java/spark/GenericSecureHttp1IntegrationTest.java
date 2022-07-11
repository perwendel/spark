package spark;

import org.junit.BeforeClass;
import spark.util.SparkTestUtil.UrlResponse;

import java.util.Map;

public class GenericSecureHttp1IntegrationTest extends GenericSecureIntegrationTest {

    @BeforeClass
    public static void setup() {
        GenericSecureIntegrationTest.setup();
    }

    @Override
    UrlResponse doMethodSecure(String requestMethod, String path, String body) throws Exception {
        return testUtil.doMethodSecure(requestMethod, path, body);
    }

    @Override
    UrlResponse doMethod(String requestMethod,
                         String path,
                         String body,
                         boolean secureConnection,
                         String acceptType,
                         Map<String, String> reqHeaders) throws Exception {
        return testUtil.doMethod(requestMethod, path, body, secureConnection, acceptType, reqHeaders);
    }
}
