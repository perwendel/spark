package spark;

import org.junit.Test;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.utils.SparkUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static spark.SparkBase.wrap;

public class SparkBaseTest
{
    @Test
    public void clearRoutes() {
        String path = "/hi";
        HttpMethod httpMethod = HttpMethod.get;
        String acceptType = SparkBase.DEFAULT_ACCEPT_TYPE;

        SparkBase.addRoute(httpMethod.name(), wrap(path, acceptType, (request, response) -> ""));

        RouteMatch actual = SparkBase.routeMatcher.findTargetForRequestedRoute(httpMethod, path, acceptType);

        assertThat(actual.getAcceptType(), is(equalTo(acceptType)));
        assertThat(actual.getHttpMethod(), is(equalTo(httpMethod)));
        assertThat(actual.getMatchUri(), is(equalTo(path)));

        SparkBase.clearRoutes();

        actual = SparkBase.routeMatcher.findTargetForRequestedRoute(httpMethod, path, acceptType);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void removeRouteWithPathAndHttpMethod() {
        String keepPath = "/keep", removePath = "/remove";
        HttpMethod httpMethod = HttpMethod.connect;
        String acceptType = SparkBase.DEFAULT_ACCEPT_TYPE;
        Route route = (request, response) -> "";

        SparkBase.addRoute(httpMethod.name(), wrap(keepPath, acceptType, route));
        SparkBase.addRoute(httpMethod.name(), wrap(removePath, acceptType, route));

        List<RouteMatch> actual = Arrays.asList(
                SparkBase.routeMatcher.findTargetForRequestedRoute(httpMethod, removePath, acceptType),
                SparkBase.routeMatcher.findTargetForRequestedRoute(httpMethod, keepPath, acceptType));

        assertThat(actual.size(), is(2));

        SparkBase.removeRoute(removePath, httpMethod.name());

        actual = SparkBase.routeMatcher.findTargetsForRequestedRoute(httpMethod, keepPath, acceptType);

        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getAcceptType(), is(equalTo(acceptType)));
        assertThat(actual.get(0).getMatchUri(), is(equalTo(keepPath)));
        assertThat(actual.get(0).getHttpMethod(), is(equalTo(httpMethod)));

        actual = SparkBase.routeMatcher.findTargetsForRequestedRoute(httpMethod, removePath, acceptType);

        assertThat(actual.size(), is(0));
    }

    @Test
    public void removeRouteWithOnlyPath() {
        String path = "/hi";
        HttpMethod keepHttpMethod = HttpMethod.connect, removeHttpMethod = HttpMethod.delete;
        String acceptType = SparkBase.DEFAULT_ACCEPT_TYPE;
        Route route = (request, response) -> "";

        SparkBase.addRoute(keepHttpMethod.name(), wrap(path, acceptType, route));
        SparkBase.addRoute(removeHttpMethod.name(), wrap(path, acceptType, route));

        List<RouteMatch> actual = Arrays.asList(
                SparkBase.routeMatcher.findTargetForRequestedRoute(keepHttpMethod, path, acceptType),
                SparkBase.routeMatcher.findTargetForRequestedRoute(removeHttpMethod, path, acceptType));

        assertThat(actual.size(), is(2));

        SparkBase.removeRoute(path);

        actual = Arrays.asList(
                SparkBase.routeMatcher.findTargetForRequestedRoute(keepHttpMethod, path, acceptType),
                SparkBase.routeMatcher.findTargetForRequestedRoute(removeHttpMethod, path, acceptType));

        assertThat(actual, hasItems(nullValue(), nullValue()));
    }
}
