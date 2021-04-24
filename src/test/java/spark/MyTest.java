package spark;

import org.junit.Before;
import org.junit.Test;

import spark.routematch.RouteMatch;
import spark.util.SparkTestUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static spark.Spark.*;

public class MyTest {

    private static final String THE_MATCHED_ROUTE = "/users/:username";

    private static SparkTestUtil http;

    HttpServletRequest servletRequest;
    HttpSession httpSession;
    Request request;

    RouteMatch match = new RouteMatch(null, "/hi", "/hi/", "text/html", null);
    RouteMatch matchWithParams = new RouteMatch(null, "/users/:username/", "/users/bob", "text/html", null);

    @Before
    public void setup() {
        http = new SparkTestUtil(4567);

        get(THE_MATCHED_ROUTE, (q,a)-> "Get filter matched");

        awaitInitialization();


        servletRequest = mock(HttpServletRequest.class);
        httpSession = mock(HttpSession.class);

        request = new Request(match, servletRequest);

    }

    @Test
    public void shouldBeAbleToGetTheMatchedPathWithTailSlash1() {
        Request request = new Request(matchWithParams, servletRequest);
        assertEquals("Should have returned the matched route", THE_MATCHED_ROUTE, request.matchedPath());
        try {
            http.get("/users/bob/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldBeAbleToGetTheMatchedPathWithTailSlash2() {
        Request request = new Request(match, servletRequest);
        assertEquals("Should have returned the matched route", "/hi", request.matchedPath());
        try {
            http.get("/hi/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
