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

import java.util.function.Consumer;

import static spark.Service.ignite;

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
 * The public methods and fields in this class should be statically imported for the semantic to make sense.
 * Ie. one should use:
 * 'post("/books")' without the prefix 'Spark.'
 *
 * @author Per Wendel
 */
public class Spark {

    // Hide constructor
    protected Spark() {
    }

    /**
     * Initializes singleton.
     */
    private static class SingletonHolder {
        private static final Service INSTANCE = ignite();
    }

    private static Service getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Statically import this for redirect utility functionality, see {@link spark.Redirect}
     */
    public static final Redirect redirect = getInstance().redirect;

    /**
     * Statically import this for static files utility functionality, see {@link spark.Service.StaticFiles}
     */
    public static final Service.StaticFiles staticFiles = getInstance().staticFiles;

    /**
     * Add a path-prefix to the routes declared in the routeGroup
     * The path() method adds a path-fragment to a path-stack, adds
     * routes from the routeGroup, then pops the path-fragment again.
     * It's used for separating routes into groups, for example:
     * path("/api/email", () -> {
     * ....post("/add",       EmailApi::addEmail);
     * ....put("/change",     EmailApi::changeEmail);
     * ....etc
     * });
     * Multiple path() calls can be nested.
     *
     * @param path       the path to prefix routes with
     * @param routeGroup group of routes (can also contain path() calls)
     */
    public static void path(String path, RouteGroup routeGroup) {
        getInstance().path(path, routeGroup);
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void get(final String path, final Route route) {
        getInstance().get(path, route);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void post(String path, Route route) {
        getInstance().post(path, route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void put(String path, Route route) {
        getInstance().put(path, route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void patch(String path, Route route) {
        getInstance().patch(path, route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void delete(String path, Route route) {
        getInstance().delete(path, route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void head(String path, Route route) {
        getInstance().head(path, route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void trace(String path, Route route) {
        getInstance().trace(path, route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void connect(String path, Route route) {
        getInstance().connect(path, route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public static void options(String path, Route route) {
        getInstance().options(path, route);
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public static void before(String path, Filter filter) {
        getInstance().before(path, filter);
    }

    /**
     * Maps an array of filters to be executed before any matching routes
     *
     * @param path    the path
     * @param filters the filters
     */

    public static void before(String path, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().before(path, filter);
        }
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public static void after(String path, Filter filter) {
        getInstance().after(path, filter);
    }

    /**
     * Maps an array of filters to be executed after any matching routes
     *
     * @param path    the path
     * @param filters The filters
     */

    public static void after(String path, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().after(path, filter);
        }
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
    public static void get(String path, String acceptType, Route route) {
        getInstance().get(path, acceptType, route);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void post(String path, String acceptType, Route route) {
        getInstance().post(path, acceptType, route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void put(String path, String acceptType, Route route) {
        getInstance().put(path, acceptType, route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void patch(String path, String acceptType, Route route) {
        getInstance().patch(path, acceptType, route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void delete(String path, String acceptType, Route route) {
        getInstance().delete(path, acceptType, route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void head(String path, String acceptType, Route route) {
        getInstance().head(path, acceptType, route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void trace(String path, String acceptType, Route route) {
        getInstance().trace(path, acceptType, route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void connect(String path, String acceptType, Route route) {
        getInstance().connect(path, acceptType, route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public static void options(String path, String acceptType, Route route) {
        getInstance().options(path, acceptType, route);
    }


    /**
     * Maps one or many filters to be executed before any matching routes
     *
     * @param filters The filters
     */
    public static void before(Filter... filters) {
        for (Filter filter : filters) {
            getInstance().before(filter);
        }
    }

    /**
     * Maps one or many filters to be executed after any matching routes
     *
     * @param filters The filters
     */
    public static void after(Filter... filters) {
        for (Filter filter : filters) {
            getInstance().after(filter);
        }
    }

    /**
     * Maps one or many filters to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filters    The filters
     */
    public static void before(String path, String acceptType, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().before(path, acceptType, filter);
        }
    }


    /**
     * Maps one or many filters to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filters    The filters
     */
    public static void after(String path, String acceptType, Filter... filters) {
        for (Filter filter : filters) {
            getInstance().after(path, acceptType, filter);
        }
    }

    /**
     * Execute after route even if the route throws exception
     *
     * @param path   the path
     * @param filter the filter
     */
    public static void afterAfter(String path, Filter filter) {
        getInstance().afterAfter(path, filter);
    }

    /**
     * Execute after any matching route even if the route throws exception
     *
     * @param filter the filter
     */
    public static void afterAfter(Filter filter) {
        getInstance().afterAfter(filter);
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
    public static void get(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().get(path, route, engine);
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void get(String path,
                           String acceptType,
                           TemplateViewRoute route,
                           TemplateEngine engine) {
        getInstance().get(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().post(path, route, engine);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void post(String path,
                            String acceptType,
                            TemplateViewRoute route,
                            TemplateEngine engine) {
        getInstance().post(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().put(path, route, engine);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void put(String path,
                           String acceptType,
                           TemplateViewRoute route,
                           TemplateEngine engine) {
        getInstance().put(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().delete(path, route, engine);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void delete(String path,
                              String acceptType,
                              TemplateViewRoute route,
                              TemplateEngine engine) {
        getInstance().delete(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().patch(path, route, engine);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void patch(String path,
                             String acceptType,
                             TemplateViewRoute route,
                             TemplateEngine engine) {
        getInstance().patch(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().head(path, route, engine);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void head(String path,
                            String acceptType,
                            TemplateViewRoute route,
                            TemplateEngine engine) {
        getInstance().head(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().trace(path, route, engine);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void trace(String path,
                             String acceptType,
                             TemplateViewRoute route,
                             TemplateEngine engine) {
        getInstance().trace(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().connect(path, route, engine);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void connect(String path,
                               String acceptType,
                               TemplateViewRoute route,
                               TemplateEngine engine) {
        getInstance().connect(path, acceptType, route, engine);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public static void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        getInstance().options(path, route, engine);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     * @param engine     the template engine
     */
    public static void options(String path,
                               String acceptType,
                               TemplateViewRoute route,
                               TemplateEngine engine) {
        getInstance().options(path, acceptType, route, engine);
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
    public static void get(String path, Route route, ResponseTransformer transformer) {
        getInstance().get(path, route, transformer);
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        getInstance().get(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void post(String path, Route route, ResponseTransformer transformer) {
        getInstance().post(path, route, transformer);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        getInstance().post(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void put(String path, Route route, ResponseTransformer transformer) {
        getInstance().put(path, route, transformer);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        getInstance().put(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void delete(String path, Route route, ResponseTransformer transformer) {
        getInstance().delete(path, route, transformer);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void delete(String path,
                              String acceptType,
                              Route route,
                              ResponseTransformer transformer) {
        getInstance().delete(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void head(String path, Route route, ResponseTransformer transformer) {
        getInstance().head(path, route, transformer);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        getInstance().head(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void connect(String path, Route route, ResponseTransformer transformer) {
        getInstance().connect(path, route, transformer);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void connect(String path,
                               String acceptType,
                               Route route,
                               ResponseTransformer transformer) {
        getInstance().connect(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void trace(String path, Route route, ResponseTransformer transformer) {
        getInstance().trace(path, route, transformer);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void trace(String path,
                             String acceptType,
                             Route route,
                             ResponseTransformer transformer) {
        getInstance().trace(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void options(String path, Route route, ResponseTransformer transformer) {
        getInstance().options(path, route, transformer);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void options(String path,
                               String acceptType,
                               Route route,
                               ResponseTransformer transformer) {
        getInstance().options(path, acceptType, route, transformer);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void patch(String path, Route route, ResponseTransformer transformer) {
        getInstance().patch(path, route, transformer);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param acceptType  the accept type
     * @param route       The route
     * @param transformer the response transformer
     */
    public static void patch(String path,
                             String acceptType,
                             Route route,
                             ResponseTransformer transformer) {
        getInstance().patch(path, acceptType, route, transformer);
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
    public static <T extends Exception> void exception(Class<T> exceptionClass, ExceptionHandler<? super T> handler) {
        getInstance().exception(exceptionClass, handler);
    }

    //////////////////////////////////////////////////
    // HALT methods
    //////////////////////////////////////////////////

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public static HaltException halt() {
        throw getInstance().halt();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public static HaltException halt(int status) {
        throw getInstance().halt(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public static HaltException halt(String body) {
        throw getInstance().halt(body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body   The body content
     */
    public static HaltException halt(int status, String body) {
        throw getInstance().halt(status, body);
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     * @deprecated replaced by {@link #ipAddress(String)}
     */
    public static void setIpAddress(String ipAddress) {
        getInstance().ipAddress(ipAddress);
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public static void ipAddress(String ipAddress) {
        getInstance().ipAddress(ipAddress);
    }

    /**
     * Set the default response transformer. All requests not using a custom transformer will use this one
     *
     * @param transformer
     */
    public static void defaultResponseTransformer(ResponseTransformer transformer) {
        getInstance().defaultResponseTransformer(transformer);
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     * @deprecated replaced by {@link #port(int)}
     */
    public static void setPort(int port) {
        getInstance().port(port);
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public static void port(int port) {
        getInstance().port(port);
    }

    /**
     * Retrieves the port that Spark is listening on.
     *
     * @return The port Spark server is listening on.
     * @throws IllegalStateException when the server is not started
     */
    public static int port() {
        return getInstance().port();
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
    public static void setSecure(String keystoreFile,
                                 String keystorePassword,
                                 String truststoreFile,
                                 String truststorePassword) {
        getInstance().secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
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
    public static void secure(String keystoreFile,
                              String keystorePassword,
                              String truststoreFile,
                              String truststorePassword) {
        getInstance().secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
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
     * @param certAlias          the default certificate Alias
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     */
    public static void secure(String keystoreFile,
                              String keystorePassword,
                              String certAlias,
                              String truststoreFile,
                              String truststorePassword) {
        getInstance().secure(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword);
    }

    /**
     * Overrides default exception handler during initialization phase
     *
     * @param initExceptionHandler The custom init exception handler
     */
    public static void initExceptionHandler(Consumer<Exception> initExceptionHandler) {
        getInstance().initExceptionHandler(initExceptionHandler);
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
     * @param needsClientCert    Whether to require client certificate to be supplied in
     *                           request
     * @param truststorePassword the trust store password
     */
    public static void secure(String keystoreFile,
                              String keystorePassword,
                              String truststoreFile,
                              String truststorePassword,
                              boolean needsClientCert) {
        getInstance().secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword, needsClientCert);
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
     * @param certAlias          the default certificate Alias
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param needsClientCert    Whether to require client certificate to be supplied in
     *                           request
     * @param truststorePassword the trust store password
     */
    public static void secure(String keystoreFile,
                              String keystorePassword,
                              String certAlias,
                              String truststoreFile,
                              String truststorePassword,
                              boolean needsClientCert) {
        getInstance().secure(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword, needsClientCert);
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads max nbr of threads.
     */
    public static void threadPool(int maxThreads) {
        getInstance().threadPool(maxThreads);
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads        max nbr of threads.
     * @param minThreads        min nbr of threads.
     * @param idleTimeoutMillis thread idle timeout (ms).
     */
    public static void threadPool(int maxThreads, int minThreads, int idleTimeoutMillis) {
        getInstance().threadPool(maxThreads, minThreads, idleTimeoutMillis);
    }

    /**
     * Sets the folder in classpath serving static files. Observe: this method
     * must be called before all other methods.
     * -
     * Note: contemplate changing tonew static files paradigm {@link spark.Service.StaticFiles}
     *
     * @param folder the folder in classpath.
     */
    public static void staticFileLocation(String folder) {
        getInstance().staticFileLocation(folder);
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     * -
     * Note: contemplate use of new static files paradigm {@link spark.Service.StaticFiles}
     *
     * @param externalFolder the external folder serving static files.
     */
    public static void externalStaticFileLocation(String externalFolder) {
        getInstance().externalStaticFileLocation(externalFolder);
    }

    /**
     * Waits for the spark server to be initialized.
     * If it's already initialized will return immediately
     */
    public static void awaitInitialization() {
        getInstance().awaitInitialization();
    }

    /**
     * Stops the Spark server and clears all routes
     */
    public static void stop() {
        getInstance().stop();
    }
    
    /**
     * Waits for the Spark server to be stopped.
     * If it's already stopped, will return immediately.
     */
    public static void awaitStop() {
    	getInstance().awaitStop();
    }

    ////////////////
    // Websockets //

    /**
     * Maps the given path to the given WebSocket handler.
     * <p>
     * This is currently only available in the embedded server mode.
     *
     * @param path    the WebSocket path.
     * @param handler the handler class that will manage the WebSocket connection to the given path.
     */
    public static void webSocket(String path, Class<?> handler) {
        getInstance().webSocket(path, handler);
    }

    public static void webSocket(String path, Object handler) {
        getInstance().webSocket(path, handler);
    }

    /**
     * Sets the max idle timeout in milliseconds for WebSocket connections.
     *
     * @param timeoutMillis The max idle timeout in milliseconds.
     */
    public static void webSocketIdleTimeoutMillis(int timeoutMillis) {
        getInstance().webSocketIdleTimeoutMillis(timeoutMillis);
    }

    /**
     * Maps 404 Not Found errors to the provided custom page
     */
    public static void notFound(String page) {
        getInstance().notFound(page);
    }

    /**
     * Maps 500 internal server errors to the provided custom page
     */
    public static void internalServerError(String page) {
        getInstance().internalServerError(page);
    }

    /**
     * Maps 404 Not Found errors to the provided route.
     */
    public static void notFound(Route route) {
        getInstance().notFound(route);
    }

    /**
     * Maps 500 internal server errors to the provided route.
     */
    public static void internalServerError(Route route) {
        getInstance().internalServerError(route);
    }

    /**
     * Initializes the Spark server. SHOULD just be used when using the Websockets functionality.
     */
    public static void init() {
        getInstance().init();
    }

    /**
     * Constructs a ModelAndView with the provided model and view name
     *
     * @param model    the model
     * @param viewName the view name
     * @return the model and view
     */
    public static ModelAndView modelAndView(Object model, String viewName) {
        return new ModelAndView(model, viewName);
    }

    /**
     * @return The approximate number of currently active threads in the embedded Jetty server
     */
    public static int activeThreadCount() {
        return getInstance().activeThreadCount();
    }

}
