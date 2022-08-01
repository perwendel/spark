package spark;

import org.junit.Before;
import org.junit.Test;

import spark.util.SparkTestUtil;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static spark.Spark.*;

// When IE broswer visit spark, there are some problems
// It is because IE will not contains q in AcceptType
// So the chosen route doesn't fit the design documentation as follows

public class Issue688Test {

    public static final String HELLO = "/hello";

    public static final String TEST="/test";

    public static final int PORT = 4567;

    private static SparkTestUtil http;

    @Before
    public void setup() {
        http = new SparkTestUtil(PORT);

        get(HELLO, (request, response) -> "Abstract");

        get(HELLO,"text/html", (request, response) -> "Specific");
    }


    // Try to fix issue 688: https://github.com/perwendel/spark/issues/688
    @Test
    public void TestWheteherMatchRight() {

        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            requestHeader.put("x-forwarded-host", "proxy.mydomain.com");

            String acceptTypeIE="text/html, application/xhtml+xml, image/jxr, */*";
            String acceptTypeChrome="text/html,application/xhtml+xml,application/xml;q=0.9," +
                "image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n";
            String acceptTypeEdge="text/html,application/xhtml+xml,application/xml;q=0.9," +
                "image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";

            SparkTestUtil.UrlResponse response = http.doMethod("GET",HELLO, "", false, acceptTypeIE, requestHeader);
            assertEquals(200, response.status);
            assertEquals("Specific", response.body);

            response = http.doMethod("GET",HELLO, "", false, acceptTypeChrome, requestHeader);
            assertEquals(200, response.status);
            assertEquals("Specific", response.body);

            response = http.doMethod("GET",HELLO, "", false, acceptTypeEdge, requestHeader);
            assertEquals(200, response.status);
            assertEquals("Specific", response.body);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
