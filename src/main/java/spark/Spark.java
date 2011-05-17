/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark;

import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

/**
 * In Spark, a route is an HTTP method paired with a URL-matching pattern. Each route is associated with a runction:
 * 
 * TODO: Add author on all files, maybe apache2 license text!!!, javadoc
 * 
 * @author Per Wendel
 */
public class Spark {

    private static boolean initialized = false;

    private static RouteMatcher routeMatcher;
    private static int port = 4567;
    
    /**
     * Set the port that Spark should listen on. Default port is 4567.
     * This has to be called before any route mapping is done.
     * 
     * @param port The port number
     */
    public synchronized static void setPort(int port) {
        if (initialized) {
            throw new IllegalStateException("This must be done before route mapping has begun");
        }
        Spark.port = port;
    }

    public static void get(Route route) {
        addRoute(HttpMethod.get.name(), route);
    }

    public static void post(Route route) {
        addRoute(HttpMethod.post.name(), route);
    }

    public static void put(Route route) {
        addRoute(HttpMethod.put.name(), route);
    }

    public static void delete(Route route) {
        addRoute(HttpMethod.delete.name(), route);
    }

    public static void head(Route route) {
        addRoute(HttpMethod.head.name(), route);
    }

    public static void trace(Route route) {
        addRoute(HttpMethod.trace.name(), route);
    }

    public static void connect(Route route) {
        addRoute(HttpMethod.connect.name(), route);
    }

    public static void options(Route route) {
        addRoute(HttpMethod.options.name(), route);
    }
    
    private static void addRoute(String httpMethod, Route route) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath() + "'", route);
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
    
    /*
     * TODO: discover new TODOs.
     * 
     * TODO: Before method for filters...check sinatra page
     * 
     * TODO: Make available as maven dependency, upload on repo etc...
     * TODO: Add *, splat possibility
     * TODO: Add validation of routes, invalid characters and stuff, also validate parameters, check static, ONGOING
     * 
     * TODO: Javadoc
     * 
     * TODO: Create maven archetype, "ONGOING"
     * TODO: Add cache-control helpers
     * 
     * advanced TODO list:
     * TODO: sessions? (use session servlet context?)
     * TODO: Add regexp URIs
     * TODO: Routes are matched in the order they are defined. The rirst route that matches the request is invoked. ???
     * 
     * Ongoing
     * 
     * Done
     * TODO: Setting Headers
     * TODO: Do we want get-prefixes for all *getters* or do we want a more ruby like approach??? (Maybe have two choices?)
     * TODO: Setting Body, Status Code
     * TODO: Add possibility to set content type on return, DONE
     * TODO: Add possibility to access HttpServletContext in method impl, DONE
     * TODO: Redirect func in web context, DONE
     * TODO: Refactor, extract interfaces, DONE
     * TODO: Figure out a nice name, DONE - SPARK
     * TODO: Add /uri/{param} possibility, DONE
     * TODO: Tweak log4j config, DONE
     * TODO: Query string in web context, DONE
     * TODO: Add URI-param fetching from webcontext ie. ?param=value&param2=...etc, AND headers, DONE
     */
}
