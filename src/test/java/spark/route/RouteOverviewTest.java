package spark.route;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.stop;
import static spark.route.RouteOverview.createHtmlForRouteTarget;
import static spark.route.RouteOverview.routes;

public class RouteOverviewTest {

    private final static int FILTER_FIELD = 0;
    private final static int FILTER_CLASS = 1;
    private final static int FILTER_METHOD_REF = 2;
    private final static int FILTER_LAMBDA = 3;
    private final static int ROUTE_FIELD = 4;
    private final static int ROUTE_CLASS = 5;
    private final static int ROUTE_METHOD_REF = 6;
    private final static int ROUTE_LAMBDA = 7;

    @BeforeClass
    public static void setup() {
        port(0);
        before("/0", RouteOverviewTest.filterField);
        before("/1", new FilterImplementer());
        before("/2", RouteOverviewTest::filterMethodRef);
        before("3",  ((request, response) -> {}));
        get("/4",    RouteOverviewTest.routeField);
        get("/5",    new RouteImplementer());
        get("/6",    RouteOverviewTest::routeMethodRef);
        get("/7",    ((request, response) -> ""));
    }

    @AfterClass
    public static void shutdown() throws Exception {
        stop();
    }

    @Test
    public void assertThat_allRoutesAreAdded() {
        assertThat(routes.size(), is(8));
    }

    @Test
    public void assertThat_unmappedRoute_doesNotWork() {
        Route unmappedRoute = (Request request, Response response) -> "";
        assertThat(createHtmlForRouteTarget(unmappedRoute), not(containsString("unmappedRoute")));
    }

    @Test
    public void assertThat_filter_field_works() {
        assertThat(routeName(FILTER_FIELD), containsString("RouteOverviewTest.filterField"));
    }

    @Test
    public void assertThat_filter_class_works() {
        assertThat(routeName(FILTER_CLASS), containsString("FilterImplementer.class"));
    }

    @Test
    public void assertThat_filter_methodRef_works() {
        assertThat(routeName(FILTER_METHOD_REF), containsString("RouteOverviewTest::filterMethodRef"));
    }

    @Test
    public void assertThat_filter_lambda_works() {
        assertThat(routeName(FILTER_LAMBDA), containsString("RouteOverviewTest???"));
    }

    @Test
    public void assertThat_route_field_works() {
        assertThat(routeName(ROUTE_FIELD), containsString("RouteOverviewTest.routeField"));
    }

    @Test
    public void assertThat_route_class_works() {
        assertThat(routeName(ROUTE_CLASS), containsString("RouteImplementer.class"));
    }

    @Test
    public void assertThat_route_methodRef_works() {
        assertThat(routeName(ROUTE_METHOD_REF), containsString("RouteOverviewTest::routeMethodRef"));
    }

    @Test
    public void assertThat_route_lambda_works() {
        assertThat(routeName(ROUTE_LAMBDA), containsString("RouteOverviewTest???"));
    }

    // fields/classes/methods to obtain names from
    private static Route routeField = (Request request, Response response) -> "";
    private static class RouteImplementer implements Route {
        @Override
        public Object handle(Request request, Response response) throws Exception {
            return "";
        }
    }
    private static String routeMethodRef(Request request, Response response) {
        return "";
    }
    private static Filter filterField = (Request request, Response response) -> {
    };
    private static class FilterImplementer implements Filter {
        @Override
        public void handle(Request request, Response response) throws Exception {
        }
    }
    private static void filterMethodRef(Request request, Response response) {
    }

    // Helper to improve test readability
    private static String routeName(int index) {
        return createHtmlForRouteTarget(routes.get(index).target).replace("<b>", ""); // Remove HTML for the test
    }

}
