package spark.route;

import org.junit.Test;

import spark.utils.SparkUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouteEntryTest {

    @Test
    public void testMatches_BeforeAndAllPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.BEFORE;
        entry.path = SparkUtils.ALL_PATHS;

        assertTrue(
                "Should return true because HTTP method is \"Before\", the methods of route and match request match," +
                        " and the path provided is same as ALL_PATHS (+/*paths)",
                entry.matches(HttpMethod.BEFORE, SparkUtils.ALL_PATHS)
        );
    }

    @Test
    public void testMatches_AfterAndAllPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.AFTER;
        entry.path = SparkUtils.ALL_PATHS;

        assertTrue(
                "Should return true because HTTP method is \"After\", the methods of route and match request match," +
                        " and the path provided is same as ALL_PATHS (+/*paths)",
                entry.matches(HttpMethod.AFTER, SparkUtils.ALL_PATHS)
        );
    }

    @Test
    public void testMatches_NotAllPathsAndDidNotMatchHttpMethod() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.POST;
        entry.path = "/test";

        assertFalse("Should return false because path names did not match",
                    entry.matches(HttpMethod.GET, "/path"));
    }

    @Test
    public void testMatches_RouteDoesNotEndWithSlash() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test";

        assertFalse("Should return false because route path does not end with a slash, does not end with " +
                            "a wildcard, and the route pah supplied ends with a slash ",
                    entry.matches(HttpMethod.GET, "/test/")
        );
    }

    @Test
    public void testMatches_PathDoesNotEndInSlash() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test/";

        assertFalse("Should return false because route path ends with a slash while path supplied as parameter does" +
                            "not end with a slash", entry.matches(HttpMethod.GET, "/test"));
    }

    @Test
    public void testMatches_MatchingPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test/";

        assertTrue("Should return true because route path and path is exactly the same",
                   entry.matches(HttpMethod.GET, "/test/"));
    }

    @Test
    public void testMatches_WithWildcardOnEntryPath() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test/*";

        assertTrue("Should return true because path specified is covered by the route path wildcard",
                   entry.matches(HttpMethod.GET, "/test/me"));
    }

    @Test
    public void testMatches_PathsDoNotMatch() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test/me";

        assertFalse("Should return false because path does not match route path",
                    entry.matches(HttpMethod.GET, "/test/other"));
    }

    @Test
    public void testMatches_longRoutePathWildcard() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.GET;
        entry.path = "/test/this/resource/*";

        assertTrue("Should return true because path specified is covered by the route path wildcard",
                   entry.matches(HttpMethod.GET, "/test/this/resource/child/id"));
    }

}