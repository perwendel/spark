package spark.route;

public interface RouteMatcher {
    void parseValidateAddRoute(String route, Object target);
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route);
}
