package spark;

import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.SimpleRouteMatcher;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

public class SparkInstance {
    public static final String SPARK_DEFAULT_IP = "0.0.0.0";
    public static final int SPARK_DEFAULT_PORT = 4567;

    private SparkServer server;
    private final RouteMatcher routeMatcher = new SimpleRouteMatcher();
    private final String ipAddress;
    private final int port;
    private final SSLConfig sslConfig;
    private final StaticFileConfig staticFileConfig;

    /**
     * Constructor
     *
     * @param ipAddress
     * @param port
     * @param sslConfig
     * @param staticFileConfig
     */
    public SparkInstance(String ipAddress, int port, SSLConfig sslConfig, StaticFileConfig staticFileConfig) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.sslConfig = sslConfig;
        this.staticFileConfig = staticFileConfig;
    }

    /**
     * Constructor
     *
     * Creates an instance listening publicly on port 4567
     */
    public SparkInstance() {
        this(SPARK_DEFAULT_IP, SPARK_DEFAULT_PORT, null, null);
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param route The route
     */
    public void get(Route route) {
        addRoute(HttpMethod.get.name(), route);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param route The route
     */
    public void post(Route route) {
        addRoute(HttpMethod.post.name(), route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param route The route
     */
    public void put(Route route) {
        addRoute(HttpMethod.put.name(), route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param route The route
     */
    public void patch(Route route) {
        addRoute(HttpMethod.patch.name(), route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param route The route
     */
    public void delete(Route route) {
        addRoute(HttpMethod.delete.name(), route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param route The route
     */
    public void head(Route route) {
        addRoute(HttpMethod.head.name(), route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param route The route
     */
    public void trace(Route route) {
        addRoute(HttpMethod.trace.name(), route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param route The route
     */
    public void connect(Route route) {
        addRoute(HttpMethod.connect.name(), route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param route The route
     */
    public void options(Route route) {
        addRoute(HttpMethod.options.name(), route);
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public void before(Filter filter) {
        addFilter(HttpMethod.before.name(), filter);
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public void after(Filter filter) {
        addFilter(HttpMethod.after.name(), filter);
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private void addRoute(String httpMethod, Route route) {
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath()
                + "'", route.getAcceptType(), route);
    }

    private void addFilter(String httpMethod, Filter filter) {
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath()
                + "'", filter.getAcceptType(), filter);
    }

    public void ignite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                server = SparkServerFactory.create(ipAddress, port, sslConfig, staticFileConfig, routeMatcher);
                server.ignite();
            }
        }).start();
    }

    public RouteMatcher getRouteMatcher() {
        return routeMatcher;
    }
}
