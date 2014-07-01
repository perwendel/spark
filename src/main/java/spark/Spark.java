/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * The main building block of a Spark application is a set of routes. A route is
 * made up of three simple pieces:
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback (request, response)</li>
 * </ul>
 * Example:
 * get("/hello", (request, response) -&#62; {
 * return "Hello World!";
 * });
 *
 * @author Per Wendel
 */
public final class Spark {
    protected static Spark.Api instance = new Spark.Api();

    // Hide constructor
    Spark() {
    }

    public static SparkBuilder builder() {
        return new SparkBuilder();
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void get(final String path, final Route route) {
        instance.addRoute(HttpMethod.get.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void post(String path, Route route) {
        instance.addRoute(HttpMethod.post.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void put(String path, Route route) {
        instance.addRoute(HttpMethod.put.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void patch(String path, Route route) {
        instance.addRoute(HttpMethod.patch.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void delete(String path, Route route) {
        instance.addRoute(HttpMethod.delete.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void head(String path, Route route) {
        instance.addRoute(HttpMethod.head.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void trace(String path, Route route) {
        instance.addRoute(HttpMethod.trace.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void connect(String path, Route route) {
        instance.addRoute(HttpMethod.connect.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public static synchronized void options(String path, Route route) {
        instance.addRoute(HttpMethod.options.name(), wrap(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public static synchronized void before(String path, Filter filter) {
        instance.addFilter(HttpMethod.before.name(), wrap(path, filter));
    }

    //////////////////////////////////////////////////
    // BEGIN route/filter mapping with accept type
    //////////////////////////////////////////////////

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public static synchronized void after(String path, Filter filter) {
        instance.addFilter(HttpMethod.after.name(), wrap(path, filter));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void get(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.get.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void post(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.post.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void put(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.put.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void patch(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.patch.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void delete(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.delete.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void head(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.head.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void trace(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.trace.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void connect(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.connect.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static synchronized void options(String path, String acceptType, Route route) {
        instance.addRoute(HttpMethod.options.name(), wrap(path, acceptType, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void before(Filter filter) {
        instance.addFilter(HttpMethod.before.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void after(Filter filter) {
        instance.addFilter(HttpMethod.after.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public static synchronized void before(String path, String acceptType, Filter filter) {
        instance.addFilter(HttpMethod.before.name(), wrap(path, acceptType, filter));
    }

    //////////////////////////////////////////////////
    // END route/filter mapping with accept type
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Template View Routes
    //////////////////////////////////////////////////

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public static synchronized void after(String path, String acceptType, Filter filter) {
        instance.addFilter(HttpMethod.after.name(), wrap(path, acceptType, filter));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void get(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void get(String path,
                                        String acceptType,
                                        TemplateViewRoute route,
                                        TemplateEngine engine) {
        instance.addRoute(HttpMethod.get.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void post(String path,
                                         String acceptType,
                                         TemplateViewRoute route,
                                         TemplateEngine engine) {
        instance.addRoute(HttpMethod.post.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void put(String path,
                                        String acceptType,
                                        TemplateViewRoute route,
                                        TemplateEngine engine) {
        instance.addRoute(HttpMethod.put.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void delete(String path,
                                           String acceptType,
                                           TemplateViewRoute route,
                                           TemplateEngine engine) {
        instance.addRoute(HttpMethod.delete.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void patch(String path,
                                          String acceptType,
                                          TemplateViewRoute route,
                                          TemplateEngine engine) {
        instance.addRoute(HttpMethod.patch.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void head(String path,
                                         String acceptType,
                                         TemplateViewRoute route,
                                         TemplateEngine engine) {
        instance.addRoute(HttpMethod.head.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void trace(String path,
                                          String acceptType,
                                          TemplateViewRoute route,
                                          TemplateEngine engine) {
        instance.addRoute(HttpMethod.trace.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void connect(String path,
                                            String acceptType,
                                            TemplateViewRoute route,
                                            TemplateEngine engine) {
        instance.addRoute(HttpMethod.connect.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static synchronized void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        instance.addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, route, engine));
    }

    //////////////////////////////////////////////////
    // END Template View Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // BEGIN Response Transforming Routes
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static synchronized void options(String path,
                                            String acceptType,
                                            TemplateViewRoute route,
                                            TemplateEngine engine) {
        instance.addRoute(HttpMethod.options.name(), TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void get(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void post(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void put(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void delete(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void delete(String path,
                                           String acceptType,
                                           Route route,
                                           ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.delete.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void head(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void connect(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void connect(String path,
                                            String acceptType,
                                            Route route,
                                            ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.connect.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void trace(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void trace(String path,
                                          String acceptType,
                                          Route route,
                                          ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.trace.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void options(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void options(String path,
                                            String acceptType,
                                            Route route,
                                            ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.options.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void patch(String path, Route route, ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, route, transformer));
    }

    //////////////////////////////////////////////////
    // END Response Transforming Routes
    //////////////////////////////////////////////////

    //////////////////////////////////////////////////
    // EXCEPTION mapper
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static synchronized void patch(String path,
                                          String acceptType,
                                          Route route,
                                          ResponseTransformer transformer) {
        instance.addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    //////////////////////////////////////////////////
    // HALT methods
    //////////////////////////////////////////////////

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param handler        The handler
     */
    public static synchronized void exception(Class<? extends Exception> exceptionClass, ExceptionHandler handler) {
        instance.exception(exceptionClass, handler);
    }

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public static void halt() {
        instance.halt();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public static void halt(int status) {
        instance.halt(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public static void halt(String body) {
        instance.halt(body);
    }

    //////////////////////////////////////////////////
    // model and view helper method
    //////////////////////////////////////////////////

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body   The body content
     */
    public static void halt(int status, String body) {
        instance.halt(status, body);
    }

    /**
     * Constructs a ModelAndView with the provided model and view name
     *
     * @param model    the model
     * @param viewName the view name
     * @return the model and view
     */
    public static ModelAndView modelAndView(Object model, String viewName) {
        return instance.modelAndView(model, viewName);
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     * @deprecated replaced by {@link #ipAddress(String)}
     */
    public static synchronized void setIpAddress(String ipAddress) {
        ipAddress(ipAddress);
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public static synchronized void ipAddress(String ipAddress) {
        instance.ip(ipAddress);
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     * @deprecated replaced by {@link #port(int)}
     */
    public static synchronized void setPort(int port) {
        port(port);
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static synchronized void port(int port) {
        instance.port(port);
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
    public static synchronized void setSecure(String keystoreFile,
                                              String keystorePassword,
                                              String truststoreFile,
                                              String truststorePassword) {
        secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
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
    public static synchronized void secure(String keystoreFile,
                                           String keystorePassword,
                                           String truststoreFile,
                                           String truststorePassword) {
        instance.secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    /**
     * Sets the folder in classpath serving static files. Observe: this method
     * must be called before all other methods.
     *
     * @param folder the folder in classpath.
     */
    public static synchronized void staticFileLocation(String folder) {
        instance.staticFileLocation(folder);
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public static synchronized void externalStaticFileLocation(String externalFolder) {
        instance.externalStaticFileLocation(externalFolder);
    }

    public static synchronized void stop() {
        instance.stop();
    }

    static synchronized void runFromServlet() {
        instance.runFromServlet();
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, final Route route) {
        return instance.wrap(path, route);
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
        return instance.wrap(path, acceptType, route);
    }

    /**
     * Wraps the filter in FilterImpl
     *
     * @param path   the path
     * @param filter the filter
     * @return the wrapped route
     */
    protected static FilterImpl wrap(final String path, final Filter filter) {
        return instance.wrap(path, filter);
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
        return instance.wrap(path, acceptType, filter);
    }

    public static ExceptionMapper getExceptionMapper() {
        return instance.exceptionMapper;
    }

    public static SimpleRouteMatcher getRouteMatcher() {
        return instance.routeMatcher;
    }

    public static class Api {
        protected static final int SPARK_DEFAULT_PORT = 4567;
        protected static final String SPARK_DEFAULT_IP = "0.0.0.0";
        protected static final String DEFAULT_ACCEPT_TYPE = "*/*";
        private static final Logger LOG = LoggerFactory.getLogger(Api.class);
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

        Api() {
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
}
