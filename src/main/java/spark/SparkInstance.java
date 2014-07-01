package spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.route.HttpMethod;
import spark.route.SimpleRouteMatcher;
import spark.servlet.SparkFilter;
import spark.utils.SparkUtils;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

public class SparkInstance {
    protected static final int SPARK_DEFAULT_PORT = 4567;
    protected static final String SPARK_DEFAULT_IP = "0.0.0.0";
    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";
    private static final Logger LOG = LoggerFactory.getLogger(SparkInstance.class);
    protected final SimpleRouteMatcher routeMatcher = new SimpleRouteMatcher();
    protected final ExceptionMapper exceptionMapper = new ExceptionMapper();
    protected final SparkFilter sparkFilter = new SparkFilter(routeMatcher, exceptionMapper);
    private int port = SPARK_DEFAULT_PORT;
    private String ipAddress = SPARK_DEFAULT_IP;
    private boolean initialized = false;
    private String keystoreFile;
    private String keystorePassword;
    private String truststoreFile;
    private String truststorePassword;
    private String staticFileFolder = null;
    private String externalStaticFileFolder = null;
    private SparkServer server;
    private boolean runFromServlet;
    private boolean servletStaticLocationSet;
    private boolean servletExternalStaticLocationSet;

    SparkInstance() {
    }

    public static SparkBuilder builder() {
        return new SparkBuilder();
    }

    private void throwBeforeRouteMappingException() {
        throw new IllegalStateException("This must be done before route mapping has begun");
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ip The ip address
     */
    protected void ip(String ip) {
        if (hasBeenInitialized()) {
            throwBeforeRouteMappingException();
        }
        this.ipAddress = ip;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    protected void port(int port) {
        if (hasBeenInitialized()) {
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
     */
    protected void secure(String keystoreFile,
                          String keystorePassword,
                          String truststoreFile,
                          String truststorePassword) {
        if (hasBeenInitialized()) {
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
     * Sets the folder in classpath serving  files. Observe: this method
     * must be called before all other methods.
     *
     * @param folder the folder in classpath.
     */
    protected void staticFileLocation(String folder) {
        if (hasBeenInitialized() && !isRunningFromServlet()) {
            throwBeforeRouteMappingException();
        }
        staticFileFolder = folder;
        if (!servletStaticLocationSet) {
            if (isRunningFromServlet()) {
                sparkFilter.configureStaticResources(staticFileFolder);
                servletStaticLocationSet = true;
            }
        } else {
            LOG.warn("Static file location has already been set");
        }
    }

    private boolean isRunningFromServlet() {
        return runFromServlet;
    }

    /**
     * Sets the external folder serving  files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving  files.
     */
    protected synchronized void externalStaticFileLocation(String externalFolder) {
        if (hasBeenInitialized() && !isRunningFromServlet()) {
            throwBeforeRouteMappingException();
        }
        externalStaticFileFolder = externalFolder;
        if (!isServletExternalStaticLocationSet()) {
            if (isRunningFromServlet()) {
                sparkFilter.configureExternalStaticResources(externalStaticFileFolder);
                servletExternalStaticLocationSet = true;
            }
        } else {
            LOG.warn("External file location has already been set");
        }
    }

    private boolean isServletExternalStaticLocationSet() {
        return servletExternalStaticLocationSet;
    }

    private boolean hasBeenInitialized() {
        return initialized;
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
        }
        initialized = false;
    }

    public void runFromServlet() {
        runFromServlet = true;
        if (!hasBeenInitialized()) {
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
    protected RouteImpl wrap(final String path, final Route route) {
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
    protected RouteImpl wrap(final String path, String acceptType, final Route route) {
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
    protected FilterImpl wrap(final String path, final Filter filter) {
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
    protected FilterImpl wrap(final String path, String acceptType, final Filter filter) {
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

    private synchronized void init() {
        if (!hasBeenInitialized()) {
            new Thread(() -> {
                server = SparkServerFactory.create(routeMatcher, exceptionMapper, hasMultipleHandlers());
                server.ignite(
                        ipAddress,
                        port,
                        keystoreFile,
                        keystorePassword,
                        truststoreFile,
                        truststorePassword,
                        staticFileFolder,
                        externalStaticFileFolder);
            }).start();
            initialized = true;
        }
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void get(final String path, final Route route) {
        this.addRoute(HttpMethod.get.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void post(String path, Route route) {
        this.addRoute(HttpMethod.post.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void put(String path, Route route) {
        this.addRoute(HttpMethod.put.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void patch(String path, Route route) {
        this.addRoute(HttpMethod.patch.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void delete(String path, Route route) {
        this.addRoute(HttpMethod.delete.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void head(String path, Route route) {
        this.addRoute(HttpMethod.head.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void trace(String path, Route route) {
        this.addRoute(HttpMethod.trace.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void connect(String path, Route route) {
        this.addRoute(HttpMethod.connect.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public synchronized void options(String path, Route route) {
        this.addRoute(HttpMethod.options.name(), wrap(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public synchronized void before(String path, Filter filter) {
        this.addFilter(HttpMethod.before.name(), wrap(path, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public synchronized void after(String path, Filter filter) {
        this.addFilter(HttpMethod.after.name(), wrap(path, filter));
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
        this.addRoute(HttpMethod.get.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void post(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.post.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void put(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.put.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void patch(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.patch.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void delete(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.delete.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void head(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.head.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void trace(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.trace.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void connect(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.connect.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public synchronized void options(String path, String acceptType, Route route) {
        this.addRoute(HttpMethod.options.name(), wrap(path, acceptType, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public synchronized void before(Filter filter) {
        this.addFilter(HttpMethod.before.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public synchronized void after(Filter filter) {
        this.addFilter(HttpMethod.after.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public synchronized void before(String path, String acceptType, Filter filter) {
        this.addFilter(HttpMethod.before.name(), wrap(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public synchronized void after(String path, String acceptType, Filter filter) {
        this.addFilter(HttpMethod.after.name(), wrap(path, acceptType, filter));
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
        this.addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public synchronized void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        this.addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, route, engine));
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
        this.addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
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
        this.addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void post(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void put(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void delete(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void head(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void connect(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void trace(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void options(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public synchronized void patch(String path, Route route, ResponseTransformer transformer) {
        this.addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
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
        this.addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
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

        exceptionMapper.map(exceptionClass, wrapper);
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

}
