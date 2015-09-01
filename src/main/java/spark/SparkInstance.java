package spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.route.HttpMethod;
import spark.route.RouteMatcherFactory;
import spark.route.SimpleRouteMatcher;
import spark.servlet.SparkFilter;
import spark.utils.SparkUtils;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

import static java.util.Objects.requireNonNull;

/**
 * Spark instance TODO: NAME
 */
final class SparkInstance {
    private static final Logger LOG = LoggerFactory.getLogger("spark.Spark");

    public static final int SPARK_DEFAULT_PORT = 4567;
    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";

    protected boolean initialized = false;

    protected int port = SPARK_DEFAULT_PORT;
    protected String ipAddress = "0.0.0.0";

    protected String keystoreFile;
    protected String keystorePassword;
    protected String truststoreFile;
    protected String truststorePassword;

    protected String staticFileFolder = null;
    protected String externalStaticFileFolder = null;

    protected Map<String, Class<?>> webSocketHandlers = null;

    protected int maxThreads = -1;
    protected int minThreads = -1;
    protected int threadIdleTimeoutMillis = -1;
    protected Optional<Integer> webSocketIdleTimeoutMillis = Optional.empty();

    protected SparkServer server;
    protected SimpleRouteMatcher routeMatcher;
    private boolean runFromServlet;

    private boolean servletStaticLocationSet;
    private boolean servletExternalStaticLocationSet;

    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     * @deprecated replaced by {@link #ipAddress(String)}
     */
    public synchronized void setIpAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.ipAddress = ipAddress;
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public synchronized void ipAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     * @deprecated replaced by {@link #port(int)}
     */
    public synchronized void setPort(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.port = port;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public synchronized void port(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.port = port;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     * @deprecated replaced by {@link #secure(String, String, String, String)}
     */
    public synchronized void setSecure(String keystoreFile,
                                       String keystorePassword,
                                       String truststoreFile,
                                       String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
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
    public synchronized void secure(String keystoreFile,
                                    String keystorePassword,
                                    String truststoreFile,
                                    String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads max nbr of threads.
     */
    public synchronized void threadPool(int maxThreads) {
        threadPool(maxThreads, -1, -1);
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads        max nbr of threads.
     * @param minThreads        min nbr of threads.
     * @param idleTimeoutMillis thread idle timeout (ms).
     */
    public synchronized void threadPool(int maxThreads, int minThreads, int idleTimeoutMillis) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        this.threadIdleTimeoutMillis = idleTimeoutMillis;
    }

    /**
     * Sets the folder in classpath serving static files. Observe: this method
     * must be called before all other methods.
     *
     * @param folder the folder in classpath.
     */
    public synchronized void staticFileLocation(String folder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException();
        }
        staticFileFolder = folder;
        if (!servletStaticLocationSet) {
            if (runFromServlet) {
                SparkFilter.configureStaticResources(staticFileFolder);
                servletStaticLocationSet = true;
            }
        } else {
            LOG.warn("Static file location has already been set");
        }
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public synchronized void externalStaticFileLocation(String externalFolder) {
        if (initialized && !runFromServlet) {
            throwBeforeRouteMappingException();
        }
        externalStaticFileFolder = externalFolder;
        if (!servletExternalStaticLocationSet) {
            if (runFromServlet) {
                SparkFilter.configureExternalStaticResources(externalStaticFileFolder);
                servletExternalStaticLocationSet = true;
            }
        } else {
            LOG.warn("External static file location has already been set");
        }
    }

    /**
     * Maps the given path to the given WebSocket handler.
     * <p>
     * This is currently only available in the embedded server mode.
     *
     * @param path    the WebSocket path.
     * @param handler the handler class that will manage the WebSocket connection to the given path.
     */
    public synchronized void webSocket(String path, Class<?> handler) {
        requireNonNull(path, "WebSocket path cannot be null");
        requireNonNull(handler, "WebSocket handler class cannot be null");
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        if (runFromServlet) {
            throw new IllegalStateException("WebSockets are only supported in the embedded server");
        }
        if (webSocketHandlers == null) {
            webSocketHandlers = new HashMap<>();
        }
        webSocketHandlers.put(path, handler);
    }

    /**
     * Sets the max idle timeout in milliseconds for WebSocket connections.
     *
     * @param timeoutMillis The max idle timeout in milliseconds.
     */
    public void webSocketIdleTimeoutMillis(int timeoutMillis) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        if (runFromServlet) {
            throw new IllegalStateException("WebSockets are only supported in the embedded server");
        }
        webSocketIdleTimeoutMillis = Optional.of(timeoutMillis);
    }

    /**
     * Waits for the spark server to be initialized.
     * If it's already initialized will return immediately
     */
    public void awaitInitialization() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.info("Interrupted by another thread");
        }
    }

    private void throwBeforeRouteMappingException() {
        throw new IllegalStateException(
                "This must be done before route mapping has begun");
    }

    private boolean hasMultipleHandlers() {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }


    /**
     * Stops the Spark server and clears all routes
     */
    public synchronized void stop() {
        if (server != null) {
            routeMatcher.clearRoutes();
            server.stop();
            latch = new CountDownLatch(1);
        }
        initialized = false;
    }

    synchronized void runFromServlet() {
        runFromServlet = true;
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            initialized = true;
        }
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, final Route route) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, route);
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, String acceptType, final Route route) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        return new RouteImpl(path, acceptType) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return route.handle(request, response);
            }
        };
    }

    /**
     * Wraps the filter in FilterImpl
     *
     * @param path   the path
     * @param filter the filter
     * @return the wrapped route
     */
    protected static FilterImpl wrap(final String path, final Filter filter) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, filter);
    }

    /**
     * Wraps the filter in FilterImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     the filter
     * @return the wrapped route
     */
    protected static FilterImpl wrap(final String path, String acceptType, final Filter filter) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        return new FilterImpl(path, acceptType) {
            @Override
            public void handle(Request request, Response response) throws Exception {
                filter.handle(request, response);
            }
        };
    }

    protected void addRoute(String httpMethod, RouteImpl route) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath()
                                                   + "'", route.getAcceptType(), route);
    }

    protected void addFilter(String httpMethod, FilterImpl filter) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath()
                                                   + "'", filter.getAcceptType(), filter);
    }

    public synchronized void init() {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            new Thread(() -> {
                server = SparkServerFactory.create(hasMultipleHandlers());
                server.ignite(
                        ipAddress,
                        port,
                        keystoreFile,
                        keystorePassword,
                        truststoreFile,
                        truststorePassword,
                        staticFileFolder,
                        externalStaticFileFolder,
                        latch,
                        maxThreads,
                        minThreads,
                        threadIdleTimeoutMillis,
                        webSocketHandlers,
                        webSocketIdleTimeoutMillis);
            }).start();
            initialized = true;
        }
    }

    // TODO: BEGIN

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void get(final String path, final Route route) {
        addRoute(HttpMethod.get.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void post(String path, Route route) {
        addRoute(HttpMethod.post.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void put(String path, Route route) {
        addRoute(HttpMethod.put.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void patch(String path, Route route) {
        addRoute(HttpMethod.patch.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void delete(String path, Route route) {
        addRoute(HttpMethod.delete.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void head(String path, Route route) {
        addRoute(HttpMethod.head.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void trace(String path, Route route) {
        addRoute(HttpMethod.trace.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void connect(String path, Route route) {
        addRoute(HttpMethod.connect.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void options(String path, Route route) {
        addRoute(HttpMethod.options.name(), wrap(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public synchronized void before(String path, Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(path, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public synchronized void after(String path, Filter filter) {
        addFilter(HttpMethod.after.name(), wrap(path, filter));
    }

    //////////////////////////////////////////////////
    // BEGIN route/filter mapping with accept type
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void get(String path, String acceptType, Route route) {
        addRoute(HttpMethod.get.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options.name(), wrap(path, acceptType, route));
    }


    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public synchronized void before(Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public synchronized void after(Filter filter) {
        addFilter(HttpMethod.after.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public synchronized void before(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public synchronized void after(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.after.name(), wrap(path, acceptType, filter));
    }

    //////////////////////////////////////////////////
    // END route/filter mapping with accept type
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Template View Routes
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void get(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void get(String path,
                                 String acceptType,
                                 TemplateViewRoute route,
                                 TemplateEngine engine) {
        addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void post(String path,
                                  String acceptType,
                                  TemplateViewRoute route,
                                  TemplateEngine engine) {
        addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void put(String path,
                                 String acceptType,
                                 TemplateViewRoute route,
                                 TemplateEngine engine) {
        addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void delete(String path,
                                    String acceptType,
                                    TemplateViewRoute route,
                                    TemplateEngine engine) {
        addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void patch(String path,
                                   String acceptType,
                                   TemplateViewRoute route,
                                   TemplateEngine engine) {
        addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void head(String path,
                                  String acceptType,
                                  TemplateViewRoute route,
                                  TemplateEngine engine) {
        addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void trace(String path,
                                   String acceptType,
                                   TemplateViewRoute route,
                                   TemplateEngine engine) {
        addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void connect(String path,
                                     String acceptType,
                                     TemplateViewRoute route,
                                     TemplateEngine engine) {
        addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public synchronized void options(String path,
                                     String acceptType,
                                     TemplateViewRoute route,
                                     TemplateEngine engine) {
        addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    //////////////////////////////////////////////////
    // END Template View Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Response Transforming Routes
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void get(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void post(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void put(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void delete(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void delete(String path,
                                    String acceptType,
                                    Route route,
                                    ResponseTransformer transformer) {
        addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void head(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void connect(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void connect(String path,
                                     String acceptType,
                                     Route route,
                                     ResponseTransformer transformer) {
        addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void trace(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void trace(String path,
                                   String acceptType,
                                   Route route,
                                   ResponseTransformer transformer) {
        addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void options(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void options(String path,
                                     String acceptType,
                                     Route route,
                                     ResponseTransformer transformer) {
        addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void patch(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void patch(String path,
                                   String acceptType,
                                   Route route,
                                   ResponseTransformer transformer) {
        addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    //////////////////////////////////////////////////
    // END Response Transforming Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // EXCEPTION mapper
    //////////////////////////////////////////////////

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param handler        The handler
     */
    public synchronized void exception(Class<? extends Exception> exceptionClass, ExceptionHandler handler) {
        // wrap
        ExceptionHandlerImpl wrapper = new ExceptionHandlerImpl(exceptionClass) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                handler.handle(exception, request, response);
            }
        };

        ExceptionMapper.getInstance().map(exceptionClass, wrapper);
    }

    //////////////////////////////////////////////////
    // HALT methods
    //////////////////////////////////////////////////

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public void halt() {
        throw new HaltException();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public void halt(int status) {
        throw new HaltException(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public void halt(String body) {
        throw new HaltException(body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body   The body content
     */
    public void halt(int status, String body) {
        throw new HaltException(status, body);
    }

    //////////////////////////////////////////////////
    // model and view helper method
    //////////////////////////////////////////////////


    /**
     * Constructs a ModelAndView with the provided model and view name
     *
     * @param model    the model
     * @param viewName the view name
     * @return the model and view
     */
    public ModelAndView modelAndView(Object model, String viewName) {
        return new ModelAndView(model, viewName);
    }

    // TODO: END

}
