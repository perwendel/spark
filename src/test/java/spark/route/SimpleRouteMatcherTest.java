package spark.route;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Tests for the SimpleRouteMatcher.
 */
public class SimpleRouteMatcherTest {

    private SimpleRouteMatcher simpleRouteMatcher;

    private final Route libraryRootTargetOne = new Route("/library", "text/html") {
        @Override
        public Object handle(final Request request, final Response response) {
            return null;
        }
    };

    private final Route libraryRootTargetTwo = new Route("/library", "application/json") {
        @Override
        public Object handle(final Request request, final Response response) {
            return null;
        }
    };

    private final Route petRootTarget = new Route("/pet") {
        @Override
        public Object handle(final Request request, final Response response) {
            return null;
        }
    };

    @Before
    public void before() {
        simpleRouteMatcher = new SimpleRouteMatcher();
    }

    @Test
    public void testFindWithNullMethod() {
        addRoute(HttpMethod.get, libraryRootTargetOne);
        addRoute(HttpMethod.post, libraryRootTargetTwo);
        addRoute(HttpMethod.get, petRootTarget); // Will be ignored

        final List<RouteMatch> results = simpleRouteMatcher.findTargetsForRequestedRoute(null, "/library", "*/*");

        boolean foundLibraryRootTargetOne = false, foundLibraryRootTargetTwo = false;
        for (final RouteMatch result : results) {
            if (result.getTarget() == libraryRootTargetOne) {
                foundLibraryRootTargetOne = true;
                continue;
            }

            if (result.getTarget() == libraryRootTargetTwo) {
                foundLibraryRootTargetTwo = true;
                continue;
            }

            fail("Unexpected item in list: " + result);
        }

        assertTrue("Should have returned libraryRootTargetOne", foundLibraryRootTargetOne);
        assertTrue("Should have returned libraryRootTargetTwo", foundLibraryRootTargetTwo);

        assertEquals("Should have the correct number of targets", 2, results.size());
    }

    @Test
    public void testFindWithNullPath() {
        addRoute(HttpMethod.get, libraryRootTargetOne);
        addRoute(HttpMethod.post, libraryRootTargetTwo); // Will be ignored
        addRoute(HttpMethod.get, petRootTarget);

        final List<RouteMatch> results = simpleRouteMatcher.findTargetsForRequestedRoute(HttpMethod.get, null, "*/*");

        boolean foundLibraryRootTargetOne = false, foundPetRoot = false;
        for (final RouteMatch result : results) {
            if (result.getTarget() == libraryRootTargetOne) {
                foundLibraryRootTargetOne = true;
                continue;
            }

            if (result.getTarget() == petRootTarget) {
                foundPetRoot = true;
                continue;
            }

            fail("Unexpected item in list: " + result);
        }

        assertTrue("Should have returned libraryRootTargetOne", foundLibraryRootTargetOne);
        assertTrue("Should have returned petRootTarget", foundPetRoot);

        assertEquals("Should have the correct number of targets", 2, results.size());
    }

    @Test
    public void testFindWithNullAcceptType() {
        addRoute(HttpMethod.get, libraryRootTargetOne);
        addRoute(HttpMethod.get, libraryRootTargetTwo);
        addRoute(HttpMethod.get, petRootTarget); // Will be ignored

        final List<RouteMatch> results = simpleRouteMatcher.findTargetsForRequestedRoute(HttpMethod.get, "/library", null);

        boolean foundLibraryRootTargetOne = false, foundLibraryRootTargetTwo = false;
        for (final RouteMatch result : results) {
            if (result.getTarget() == libraryRootTargetOne) {
                foundLibraryRootTargetOne = true;
                continue;
            }

            if (result.getTarget() == libraryRootTargetTwo) {
                foundLibraryRootTargetTwo = true;
                continue;
            }

            fail("Unexpected item in list: " + result);
        }

        assertTrue("Should have returned libraryRootTargetOne", foundLibraryRootTargetOne);
        assertTrue("Should have returned libraryRootTargetTwo", foundLibraryRootTargetTwo);

        assertEquals("Should have the correct number of targets", 2, results.size());
    }

    @Test
    public void testFindWithExactMatch() {
        addRoute(HttpMethod.get, libraryRootTargetOne);
        addRoute(HttpMethod.get, libraryRootTargetTwo);
        addRoute(HttpMethod.get, petRootTarget);

        final List<RouteMatch> results = simpleRouteMatcher.findTargetsForRequestedRoute(HttpMethod.get, "/library", "application/json");

        assertEquals("Should have the correct number of targets", 1, results.size());

        assertSame("Should have returned libraryRootTargetOne", libraryRootTargetTwo, results.get(0).getTarget());
    }

    @Test
    public void testFindAll() {
        addRoute(HttpMethod.get, libraryRootTargetOne);
        addRoute(HttpMethod.get, libraryRootTargetTwo);
        addRoute(HttpMethod.get, petRootTarget);

        final List<RouteMatch> results = simpleRouteMatcher.findTargetsForRequestedRoute(null, null, null);

        boolean foundLibraryRootTargetOne = false, foundLibraryRootTargetTwo = false, foundPetRootTarget = false;
        for (final RouteMatch result : results) {
            if (result.getTarget() == libraryRootTargetOne) {
                foundLibraryRootTargetOne = true;
                continue;
            }

            if (result.getTarget() == libraryRootTargetTwo) {
                foundLibraryRootTargetTwo = true;
                continue;
            }

            if (result.getTarget() == petRootTarget) {
                foundPetRootTarget = true;
                continue;
            }

            fail("Unexpected item in list: " + result);
        }

        assertTrue("Should have returned libraryRootTargetOne", foundLibraryRootTargetOne);
        assertTrue("Should have returned libraryRootTargetTwo", foundLibraryRootTargetTwo);
        assertTrue("Should have returned petRootTarget", foundPetRootTarget);

        assertEquals("Should have the correct number of targets", 3, results.size());
    }

    private void addRoute(HttpMethod httpMethod, Route route) {
        simpleRouteMatcher.parseValidateAddRoute(httpMethod.name() + " '" + route.getPath() + "'", route.getAcceptType(), route);
    }
}
