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

import spark.route.HttpMethod;
import spark.route.RouteMatcher;
import spark.route.RouteMatcherFactory;
import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

/**
 * The main building block of a Spark application is a set of routes. A route is made up of three simple pieces:
 * 
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback ( handle(Request request, Response response) )</li>
 * </ul>
 * 
 * Example:
 * <pre>
 * {@code
 * Spark.get(new Route("/hello") {
 *    public Object handle(Request request, Response response) {
 *       return "Hello World!";
 *    }
 * });
 * </pre> 
  <code>
      
   </code>
 * 
 * @author Per Wendel
 */
public class Spark {

    private static boolean initialized = false;

    private static RouteMatcher routeMatcher;
    private static int port = 4567;
    
    /**
     * Set the port that Spark should listen on. If not called the default port is 4567.
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

    /**
     * Map the route for HTTP GET requests
     * 
     * @param route The route
     */
    public static void get(Route route) {
        addRoute(HttpMethod.get.name(), route);
    }

    /**
     * Map the route for HTTP POST requests
     * 
     * @param route The route
     */
    public static void post(Route route) {
        addRoute(HttpMethod.post.name(), route);
    }

    /**
     * Map the route for HTTP PUT requests
     * 
     * @param route The route
     */
    public static void put(Route route) {
        addRoute(HttpMethod.put.name(), route);
    }

    /**
     * Map the route for HTTP DELETE requests
     * 
     * @param route The route
     */
    public static void delete(Route route) {
        addRoute(HttpMethod.delete.name(), route);
    }

    /**
     * Map the route for HTTP HEAD requests
     * 
     * @param route The route
     */
    public static void head(Route route) {
        addRoute(HttpMethod.head.name(), route);
    }

    /**
     * Map the route for HTTP TRACE requests
     * 
     * @param route The route
     */
    public static void trace(Route route) {
        addRoute(HttpMethod.trace.name(), route);
    }

    /**
     * Map the route for HTTP CONNECT requests
     * 
     * @param route The route
     */
    public static void connect(Route route) {
        addRoute(HttpMethod.connect.name(), route);
    }
    
    /**
     * Map the route for HTTP OPTIONS requests
     * 
     * @param route The route
     */
    public static void options(Route route) {
        addRoute(HttpMethod.options.name(), route);
    }
    
    /**
     * Maps a filter to be executed before any matching routes
     * 
     * @param filter The filter
     */
    public static void before(Filter filter) {
        addFilter(HttpMethod.before.name(), filter);
    }
    
    private static void addRoute(String httpMethod, Route route) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath() + "'", route);
    }
    
    private static void addFilter(String httpMethod, Filter filter) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath() + "'", filter);
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
