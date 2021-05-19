package spark;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import spark.http.matching.Configuration;

import static spark.Spark.*;

public class issue911Test {
    Configuration configuration;

    //https://github.com/perwendel/spark/issues/911
    @Before
    public void init(){
        configuration = Configuration.getConfiguration();
    }

    //https://github.com/perwendel/spark/issues/911
    @Test
    public void TestResponseDefaultContentType() throws Exception {
        get("/hello", (request, response) -> {
            Assert.assertEquals("text/html; charset=utf-8", response.type());
            return "Hello World!";
        });
    }

    //https://github.com/perwendel/spark/issues/911
    @Test
    public void TestResponseTypeModifiedByBefore() throws Exception {
        before("/hello", (request, response) -> response.type("application/json"));
        get("/hello", (request, response) -> {
            Assert.assertEquals("text/html; charset=utf-8", configuration.getDefaultContentType());
            Assert.assertEquals("application/json", response.type());
            return "Hello World!";
        });
    }

    //https://github.com/perwendel/spark/issues/911
    @Test
    public void TestSetDefaultContentType() throws Exception {
        configuration.setDefaultContentType("text/html");
        get("/hello", (request, response) -> {
            Assert.assertEquals("text/html",configuration.getDefaultContentType());
            Assert.assertEquals("text/html", response.type());
            return "Hello World!";
        });
    }
}
