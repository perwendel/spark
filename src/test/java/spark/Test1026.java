package spark;

import org.junit.Before;
import org.junit.Test;

import spark.util.SparkTestUtil;

import static org.junit.Assert.*;
import static spark.Spark.*;

// Try to fix issue 1026: https://github.com/perwendel/spark/issues/1026
// If your path of URL contain this character %, please do not put the URL into address bar in browser directly.
// Encode the URL with JavaScrip firstly, url_encode = encodeURI(URL)   (JavaScrip language)
// For example, encodeURI("http://localhost:4567/api/v1/permissionrole/permission/get%2Fabc中文한국어にほんごلغة عربية")
// Then put the encodeURI into the address bar in browser, you will get correct answer

public class Test1026 {
    private static final String ROUTE = "/api/v1/permissionrole/permission/get%2Fabc中文한국어にほんごلغة عربية";
    private static SparkTestUtil http;

    @Before
    public void setup() {
        http = new SparkTestUtil(4567);
        get(ROUTE, (q,a)-> "Get filter matched");
        awaitInitialization();
    }

    @Test
    public void testUrl() throws Exception {
        SparkTestUtil.UrlResponse response = http.get(ROUTE);
        assertEquals(200,response.status);
    }
}
