package spark.route;

import java.lang.reflect.Method;

public interface RouteMatcher {
    void parseValidateAddRoute(String route, Method target);
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route);
}
