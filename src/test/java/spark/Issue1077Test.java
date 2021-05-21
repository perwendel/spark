package spark;

import org.junit.Before;
import org.junit.Test;

import spark.util.SparkTestUtil;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static spark.Spark.*;

// Try to fix issue 1077: https://github.com/perwendel/spark/issues/1077
// I am not sure whether it is a bug, because it is tagged as Bug ..?
// But I think it conflict with the documentation, so I try to fix it
// In short, we expect the input and output are:
// curl -i -H "Accept: application/json" http://localhost:4567/hello : Hello application json
// curl -i -H "Accept: text/html" http://localhost:4567/hello : Go Away!!!
// curl http://localhost:4567/hello : Hello application json
// The first and second are right, but now the last command will get output: Go Away!!!
// I think it is not reasonable because the empty acceptType should match every possibilities
// so with the earliest match, it should match the first possible acceptType
// TestWheteherMatchRight() checks whether it matchs right
// TestWhetherMatchFirst() checks whether it matchs first

public class Issue1077Test {

    public static final String HELLO = "/hello";

    public static final String TEST="/test";

    public static final int PORT = 4567;

    private static final SparkTestUtil http = new SparkTestUtil(PORT);

    @Before
    public void setup() {

        get(HELLO,"application/json", (request, response) -> "Hello application json");

        get(HELLO,"text/json", (request, response) -> "Hello text json");

        get(HELLO, (request, response) -> {
            response.status(406);
            return "Go Away!!!";
        });

        get(TEST,"text/json", (request, response) -> "Hello text json");

        get(TEST,"application/json", (request, response) -> "Hello application json");

        get(TEST, (request, response) -> {
            response.status(406);
            return "Go Away!!!";
        });
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/1077
    @Test
    public void testWheteherMatchRight() {

        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            requestHeader.put("x-forwarded-host", "proxy.mydomain.com");

            SparkTestUtil.UrlResponse response = http.doMethod("GET",HELLO, "", false, "*/*", requestHeader);
            assertEquals(200, response.status);
            assertEquals("Hello application json", response.body);

            response = http.doMethod("GET",HELLO, "", false, "text/json", requestHeader);
            assertEquals(200, response.status);
            assertEquals("Hello text json", response.body);

            response = http.doMethod("GET",HELLO, "", false, "text/html*", requestHeader);
            assertEquals(406, response.status);
            assertEquals("Go Away!!!", response.body);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CS304 Issue link: https://github.com/perwendel/spark/issues/1077
    @Test
    public void testWhetherMatchFirst() {
        try {
            Map<String, String> requestHeader = new HashMap<>();
            requestHeader.put("Host", "localhost:" + PORT);
            requestHeader.put("User-Agent", "curl/7.55.1");
            requestHeader.put("x-forwarded-host", "proxy.mydomain.com");

            SparkTestUtil.UrlResponse response = http.doMethod("GET",TEST, "", false, "*/*", requestHeader);
            assertEquals(200, response.status);
            assertEquals("Hello text json", response.body);

            response = http.doMethod("GET",HELLO, "", false, "*/*", requestHeader);
            assertEquals(200, response.status);
            assertEquals("Hello application json", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
