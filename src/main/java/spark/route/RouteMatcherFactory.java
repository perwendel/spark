package spark.route;

import org.apache.log4j.Logger;

public class RouteMatcherFactory {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(RouteMatcherFactory.class);

    private static RouteMatcher routeMatcher = null;

    public static synchronized RouteMatcher get() {
        if (routeMatcher == null) {
            LOG.info("creates RouteMatcher");
            routeMatcher = new RouteMatcherImpl();
        }
        return routeMatcher;
    }

}
