/*
 * Copyright 2015 - Per Wendel
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
import spark.utils.SparkUtils;

/**
 * Routable abstract class. Lets extending classes inherit default routable functionality.
 */
abstract class Routable {

    /**
     * Adds a route
     *
     * @param httpMethod the HTTP method
     * @param route      the route implementation
     */
    protected abstract void addRoute(String httpMethod, RouteImpl route);

    /**
     * Adds a filter
     *
     * @param httpMethod the HTTP method
     * @param filter     the route implementation
     */
    protected abstract void addFilter(String httpMethod, FilterImpl filter);

    /////////////////////////////
    // Default implementations //

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public void get(final String path, final Route route) {
        addRoute(HttpMethod.get.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public void post(String path, Route route) {
        addRoute(HttpMethod.post.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public void put(String path, Route route) {
        addRoute(HttpMethod.put.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public void patch(String path, Route route) {
        addRoute(HttpMethod.patch.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public void delete(String path, Route route) {
        addRoute(HttpMethod.delete.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public void head(String path, Route route) {
        addRoute(HttpMethod.head.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public void trace(String path, Route route) {
        addRoute(HttpMethod.trace.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public void connect(String path, Route route) {
        addRoute(HttpMethod.connect.name(), RouteImpl.create(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public void options(String path, Route route) {
        addRoute(HttpMethod.options.name(), RouteImpl.create(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public void before(String path, Filter filter) {
        addFilter(HttpMethod.before.name(), FilterImpl.create(path, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public void after(String path, Filter filter) {
        addFilter(HttpMethod.after.name(), FilterImpl.create(path, filter));
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
    public void get(String path, String acceptType, Route route) {
        addRoute(HttpMethod.get.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect.name(), RouteImpl.create(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options.name(), RouteImpl.create(path, acceptType, route));
    }


    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public void before(Filter filter) {
        addFilter(HttpMethod.before.name(), FilterImpl.create(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public void after(Filter filter) {
        addFilter(HttpMethod.after.name(), FilterImpl.create(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public void before(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.before.name(), FilterImpl.create(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public void after(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.after.name(), FilterImpl.create(path, acceptType, filter));
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
    public void get(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void get(String path,
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
    public void post(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void post(String path,
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
    public void put(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void put(String path,
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
    public void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void delete(String path,
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
    public void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void patch(String path,
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
    public void head(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void head(String path,
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
    public void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void trace(String path,
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
    public void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void connect(String path,
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
    public void options(String path, TemplateViewRoute route, TemplateEngine engine) {
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
    public void options(String path,
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
    public void get(String path, Route route, ResponseTransformer transformer) {
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
    public void get(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.get.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void post(String path, Route route, ResponseTransformer transformer) {
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
    public void post(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void put(String path, Route route, ResponseTransformer transformer) {
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
    public void put(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void delete(String path, Route route, ResponseTransformer transformer) {
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
    public void delete(String path,
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
    public void head(String path, Route route, ResponseTransformer transformer) {
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
    public void head(String path, String acceptType, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void connect(String path, Route route, ResponseTransformer transformer) {
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
    public void connect(String path,
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
    public void trace(String path, Route route, ResponseTransformer transformer) {
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
    public void trace(String path,
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
    public void options(String path, Route route, ResponseTransformer transformer) {
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
    public void options(String path,
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
    public void patch(String path, Route route, ResponseTransformer transformer) {
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
    public void patch(String path,
                      String acceptType,
                      Route route,
                      ResponseTransformer transformer) {
        addRoute(HttpMethod.patch.name(), ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

}
