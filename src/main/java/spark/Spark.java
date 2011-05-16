package spark;

import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

// get, post, put, delete, head, trace, connect, options
/**
 * TODO: Routes are matched in the order they are defined. The rirst route that matches the request is invoked. ???
 * 
 * In Spark, a route is an HTTP method paired with a URL-matching pattern. Each route is associated with a runction:
 */
public class Spark {

    private static boolean initialized = false;

    private static RouteMatcher routeMatcher;
    private static int port = 4567;
    
    public synchronized static void setPort(int port) {
        if (initialized) {
            throw new IllegalStateException("This must be done before route mapping has begun");
        }
        Spark.port = port;
    }
    
    private synchronized static final void init() {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SparkServer server = SparkServerFactory.create();
                    server.ignite(port);
                }
            }).start();
            initialized = true;
        }
    }

    public static void get(Route r) {
        addRoute(HttpMethod.get.name(), r);
    }

    public static void post(Route r) {
        addRoute(HttpMethod.post.name(), r);
    }

    public static void put(Route r) {
        addRoute(HttpMethod.put.name(), r);
    }

    public static void delete(Route r) {
        addRoute(HttpMethod.delete.name(), r);
    }

    public static void head(Route r) {
        addRoute(HttpMethod.head.name(), r);
    }

    public static void trace(Route r) {
        addRoute(HttpMethod.trace.name(), r);
    }

    public static void connect(Route r) {
        addRoute(HttpMethod.connect.name(), r);
    }

    public static void options(Route r) {
        addRoute(HttpMethod.options.name(), r);
    }
    
    private static void addRoute(String httpMethod, Route r) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + r.getRoute() + "'", r);
    }

}
