package spark;

import org.junit.BeforeClass;
import spark.util.SparkTestUtil.UrlResponse;

import java.io.IOException;
import java.util.Map;

public class GenericHttp1IntegrationTest extends GenericIntegrationTest {

    @BeforeClass
    public static void setup() throws IOException {
        GenericIntegrationTest.setup();
    }

    @Override
    UrlResponse doMethod(String requestMethod, String path, String body, String acceptType) throws Exception {
        return testUtil.doMethod(requestMethod, path, body, acceptType);
    }

    @Override
    UrlResponse doMethod(String requestMethod, String path, String body) throws Exception {
        return testUtil.doMethod(requestMethod, path, body);
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
