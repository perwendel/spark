package spark;

import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

// get, post, put, delete, head, trace, connect, options
/**
 * TODO: Routes are matched in the order they are defined. The first route that matches the request is invoked. ???
 * 
 * In Spark, a route is an HTTP method paired with a URL-matching pattern. Each route is associated with a function:
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

    public static void get(String route, Route f) {
        addRoute(HttpMethod.get.name(), route, f);
    }

    public static void post(String route, Route f) {
        addRoute(HttpMethod.post.name(), route, f);
    }

    public static void put(String route, Route f) {
        addRoute(HttpMethod.put.name(), route, f);
    }

    public static void delete(String route, Route f) {
        addRoute(HttpMethod.delete.name(), route, f);
    }

    public static void head(String route, Route f) {
        addRoute(HttpMethod.head.name(), route, f);
    }

    public static void trace(String route, Route f) {
        addRoute(HttpMethod.trace.name(), route, f);
    }

    public static void connect(String route, Route f) {
        addRoute(HttpMethod.connect.name(), route, f);
    }

    public static void options(String route, Route f) {
        addRoute(HttpMethod.options.name(), route, f);
    }
    
    private static void addRoute(String httpMethod, String route, Route f) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route + "'", f);
    }

}
