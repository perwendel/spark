
package spark.route;

import org.junit.Test;
import spark.utils.SparkUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouteEntryTest {

    @Test
    public void testMatches_BeforeAndAllPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.before;
        entry.path = SparkUtils.ALL_PATHS;

        assertTrue(entry.matches(HttpMethod.before, SparkUtils.ALL_PATHS));
    }

    @Test
    public void testMatches_AfterAndAllPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.after;
        entry.path = SparkUtils.ALL_PATHS;

        assertTrue(entry.matches(HttpMethod.after, SparkUtils.ALL_PATHS));
    }

    @Test
    public void testMatches_NotAllPathsAndDidNotMatchHttpMethod() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.post;
        entry.path = "/test";

        assertFalse(entry.matches(HttpMethod.get, "/path"));
    }

    @Test
    public void testMatches_RouteDoesNotEndWithSlash() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test";

        assertFalse(entry.matches(HttpMethod.get, "/test/"));
    }

    @Test
    public void testMatches_PathDoesNotEndInSlash() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test/";

        assertFalse(entry.matches(HttpMethod.get, "/test"));
    }

    @Test
    public void testMatches_MatchingPaths() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test/";

        assertTrue(entry.matches(HttpMethod.get, "/test/"));
    }

    @Test
    public void testMatches_WithWildcardOnEntryPath() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test/*";

        assertTrue(entry.matches(HttpMethod.get, "/test/me"));
    }

    @Test
    public void testMatches_PathsDoNotMatch() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test/me";

        assertFalse(entry.matches(HttpMethod.get, "/test/other"));
    }

    @Test
    public void testMatches_routePathWildcard() {

        RouteEntry entry = new RouteEntry();
        entry.httpMethod = HttpMethod.get;
        entry.path = "/test/this/resource/*";

        assertTrue(entry.matches(HttpMethod.get, "/test/this/resource/child/id"));
    }

}