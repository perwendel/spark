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

import spark.exception.ExceptionHandler;
import spark.exception.ExceptionMapper;
import spark.route.HttpMethod;
import spark.utils.SparkUtils;

/**
 * The main building block of a Spark application is a set of routes. A route is
 * made up of three simple pieces:
 * <p/>
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback ( handle(Request request, Response response) )</li>
 * </ul>
 * <p/>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * Spark.get(new Route("/hello") {
 *    public Object handle(Request request, Response response) {
 *       return "Hello World!";
 *    }
 * });
 * </pre>
 * <p/>
 * <code>
 * <p/>
 * </code>
 *
 * @author Per Wendel
 */
public final class Spark extends SparkBase {
    // Hide constructor
    private Spark() {
    }

    /**
     * Map the route for HTTP GET requests
     *
     * @param route The route
     */
    public static synchronized void get(String path, Route route) {
        addRoute(HttpMethod.get.name(), path, route);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param route The route
     */
    public static synchronized void post(String path, Route route) {
        addRoute(HttpMethod.post.name(), path, route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param route The route
     */
    public static synchronized void put(String path, Route route) {
        addRoute(HttpMethod.put.name(), path, route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param route The route
     */
    public static synchronized void patch(String path, Route route) {
        addRoute(HttpMethod.patch.name(), path, route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param route The route
     */
    public static synchronized void delete(String path, Route route) {
        addRoute(HttpMethod.delete.name(), path, route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param route The route
     */
    public static synchronized void head(String path, Route route) {
        addRoute(HttpMethod.head.name(), path, route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param route The route
     */
    public static synchronized void trace(String path, Route route) {
        addRoute(HttpMethod.trace.name(), path, route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param route The route
     */
    public static synchronized void connect(String path, Route route) {
        addRoute(HttpMethod.connect.name(), path, route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param route The route
     */
    public static synchronized void options(String path, Route route) {
        addRoute(HttpMethod.options.name(), path, route);
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void before(String path, Filter filter) {
        addFilter(HttpMethod.before.name(), path, filter);
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void after(String path, Filter filter) {
        addFilter(HttpMethod.after.name(), path, filter);
    }

    //////////////////////////////////////////////////
    // BEGIN route/filter mapping with accept type
    //////////////////////////////////////////////////

    /**
     * Map the route for HTTP GET requests
     *
     * @param route The route
     */
    public static synchronized void get(String path, String acceptType, Route route) {
        addRoute(HttpMethod.get.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param route The route
     */
    public static synchronized void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param route The route
     */
    public static synchronized void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param route The route
     */
    public static synchronized void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param route The route
     */
    public static synchronized void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param route The route
     */
    public static synchronized void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param route The route
     */
    public static synchronized void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param route The route
     */
    public static synchronized void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect.name(), path, acceptType, route);
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param route The route
     */
    public static synchronized void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options.name(), path, acceptType, route);
    }



    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void before(Filter filter) {
        addFilter(HttpMethod.before.name(), SparkUtils.ALL_PATHS, filter);
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void after(Filter filter) {
        addFilter(HttpMethod.after.name(), SparkUtils.ALL_PATHS, filter);
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void before(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.before.name(), path, acceptType, filter);
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public static synchronized void after(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.after.name(), path, acceptType, filter);
    }

    //////////////////////////////////////////////////
    // END route/filter mapping with accept type
    //////////////////////////////////////////////////

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param handler The handler
     */
    public static synchronized void exception(ExceptionHandler handler) {
        ExceptionMapper.getInstance().map(handler.exceptionClass(), handler);
    }

    // HALT METHODS

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public static void halt() {
        throw new HaltException();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public static void halt(int status) {
        throw new HaltException(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public static void halt(String body) {
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
    public static void halt(int status, String body) {
        throw new HaltException(status, body);
    }






}
