package spark;

import spark.route.RouteMatcherFactory;
import spark.route.SimpleRouteMatcher;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

/**
 * Spark base class
 */
public abstract class SparkBase {
    public static final int SPARK_DEFAULT_PORT = 4567;
    private static final String DEFAULT_ACCEPT_TYPE = "*/*";

    protected static boolean initialized = false;

    protected static int port = SPARK_DEFAULT_PORT;
    protected static String ipAddress = "0.0.0.0";

    protected static String keystoreFile;
    protected static String keystorePassword;
    protected static String truststoreFile;
    protected static String truststorePassword;

    protected static String staticFileFolder = null;
    protected static String externalStaticFileFolder = null;

    protected static SparkServer server;
    protected static SimpleRouteMatcher routeMatcher;

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public static synchronized void setIpAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static synchronized void setPort(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        Spark.port = port;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * <p/>
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     */
    public static synchronized void setSecure(String keystoreFile,
                                              String keystorePassword, String truststoreFile,
                                              String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        Spark.keystoreFile = keystoreFile;
        Spark.keystorePassword = keystorePassword;
        Spark.truststoreFile = truststoreFile;
        Spark.truststorePassword = truststorePassword;
    }

    /**
     * Sets the folder in classpath serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param folder the folder in classpath.
     */
    public static synchronized void staticFileLocation(String folder) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        staticFileFolder = folder;
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public static synchronized void externalStaticFileLocation(String externalFolder) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        externalStaticFileFolder = externalFolder;
    }

    private static void throwBeforeRouteMappingException() {
        throw new IllegalStateException(
                "This must be done before route mapping has begun");
    }

    private static boolean hasMultipleHandlers() {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }


    /**
     * Stops the Spark server and clears all routes
     */
    public static synchronized void stop() {
        if (server != null) {
            routeMatcher.clearRoutes();
            server.stop();
        }
        initialized = false;
    }

    static synchronized void runFromServlet() {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            initialized = true;
        }
    }

    protected static RouteImpl wrap(final String path, final Route route) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, route);
    }

    protected static RouteImpl wrap(final String path, String acceptType, final Route route) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        RouteImpl impl = new RouteImpl(path, acceptType) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return route.handle(request, response);
            }
        };
        return impl;
    }

    protected static FilterImpl wrap(final String path, final Filter filter) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, filter);
    }

    protected static FilterImpl wrap(final String path, String acceptType, final Filter filter) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        FilterImpl impl = new FilterImpl(path, acceptType) {
            @Override
            public void handle(Request request, Response response) throws Exception {
                filter.handle(request, response);
            }
        };
        return impl;
    }

    protected static void addRoute(String httpMethod, RouteImpl route) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath()
                                                   + "'", route.getAcceptType(), route);
    }

    protected static void addFilter(String httpMethod, FilterImpl filter) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath()
                                                   + "'", filter.getAcceptType(), filter);
    }

    private static synchronized void init() {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    server = SparkServerFactory.create(hasMultipleHandlers());
                    server.ignite(
                            ipAddress,
                            port,
                            keystoreFile,
                            keystorePassword,
                            truststoreFile,
                            truststorePassword,
                            staticFileFolder,
                            externalStaticFileFolder);
                }
            }).start();
            initialized = true;
        }
    }

}
