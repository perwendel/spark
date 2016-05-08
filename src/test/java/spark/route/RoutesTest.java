package spark.route;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoutesTest {

    @Test
    public void testParseValidateAddRoute_whenHttpMethodIsValid_thenAddRoute() {
        //given
        String route = "get'/hello'";
        String acceptType = "*/*";
        Object target = new Object();

        RouteEntry expectedRouteEntry = new RouteEntry();
        expectedRouteEntry.acceptedType = acceptType;
        expectedRouteEntry.httpMethod = HttpMethod.get;
        expectedRouteEntry.path = "/hello";
        expectedRouteEntry.target = target;
        List<RouteEntry> expectedRoutes = new ArrayList<>();
        expectedRoutes.add(expectedRouteEntry);

        Routes simpleRouteMatcher = Routes.create();
        simpleRouteMatcher.add(route, acceptType, target);

        //then
        List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
        assertTrue("Should return true because http method is valid and the route should be added to the list",
                   Util.equals(routes, expectedRoutes));

    }

    @Test
    public void testParseValidateAddRoute_whenHttpMethodIsInvalid_thenDoNotAddRoute() {
        //given
        String route = "test'/hello'";
        String acceptType = "*/*";
        Object target = new Object();

        Routes simpleRouteMatcher = Routes.create();
        simpleRouteMatcher.add(route, acceptType, target);

        //then
        List<RouteEntry> routes = Whitebox.getInternalState(simpleRouteMatcher, "routes");
        assertEquals("Should return 0 because test is not a valid http method, so the route is not added to the list",
                     routes.size(), 0);
    }
}
