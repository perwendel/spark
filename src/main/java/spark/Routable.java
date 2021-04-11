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

    private ResponseTransformer defaultResponseTransformer;

    /**
     * Adds a route
     *
     * @param httpMethod the HTTP method
     * @param route      the route implementation
     */
    protected abstract void addRoute(HttpMethod httpMethod, RouteImpl route);

    @Deprecated
    protected abstract void addRoute(String httpMethod, RouteImpl route);

    /**
     * Adds a filter
     *
     * @param httpMethod the HTTP method
     * @param filter     the route implementation
     */
    protected abstract void addFilter(HttpMethod httpMethod, FilterImpl filter);

    @Deprecated
    protected abstract void addFilter(String httpMethod, FilterImpl filter);

    /////////////////////////////
    // Default implementations //

    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    public void get(String path, Route route) {
        addRoute(HttpMethod.get, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    public void post(String path, Route route) {
        addRoute(HttpMethod.post, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    public void put(String path, Route route) {
        addRoute(HttpMethod.put, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    public void patch(String path, Route route) {
        addRoute(HttpMethod.patch, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    public void delete(String path, Route route) {
        addRoute(HttpMethod.delete, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    public void head(String path, Route route) {
        addRoute(HttpMethod.head, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    public void trace(String path, Route route) {
        addRoute(HttpMethod.trace, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    public void connect(String path, Route route) {
        addRoute(HttpMethod.connect, createRouteImpl(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    public void options(String path, Route route) {
        addRoute(HttpMethod.options, createRouteImpl(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public void before(String path, Filter filter) {
        addFilter(HttpMethod.before, FilterImpl.create(path, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    public void after(String path, Filter filter) {
        addFilter(HttpMethod.after, FilterImpl.create(path, filter));
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
        addRoute(HttpMethod.get, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect, createRouteImpl(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    public void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options, createRouteImpl(path, acceptType, route));
    }


    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    public void before(Filter filter) {
        addFilter(HttpMethod.before, FilterImpl.create(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    public void after(Filter filter) {
        addFilter(HttpMethod.after, FilterImpl.create(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public void before(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.before, FilterImpl.create(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    public void after(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.after, FilterImpl.create(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes even if the route throws any exception
     *
     * @param filter The filter
     */
    public void afterAfter(Filter filter) {
        addFilter(HttpMethod.afterafter, FilterImpl.create(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes even if the route throws any exception
     *
     * @param filter The filter
     */
    public void afterAfter(String path, Filter filter) {
        addFilter(HttpMethod.afterafter, FilterImpl.create(path, filter));
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
        addRoute(HttpMethod.get, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.get, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void post(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.post, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.post, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void put(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.put, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.put, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void delete(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.delete, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.delete, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void patch(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.patch, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.patch, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void head(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.head, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.head, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void trace(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.trace, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.trace, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void connect(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.connect, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.connect, TemplateViewRouteImpl.create(path, acceptType, route, engine));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path   the path
     * @param route  The route
     * @param engine the template engine
     */
    public void options(String path, TemplateViewRoute route, TemplateEngine engine) {
        addRoute(HttpMethod.options, TemplateViewRouteImpl.create(path, route, engine));
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
        addRoute(HttpMethod.options, TemplateViewRouteImpl.create(path, acceptType, route, engine));
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
        addRoute(HttpMethod.get, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.get, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void post(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.post, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.post, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void put(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.put, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.put, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void delete(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.delete, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.delete, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void head(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.head, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.head, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void connect(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.connect, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.connect, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void trace(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.trace, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.trace, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void options(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.options, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.options, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path        the path
     * @param route       The route
     * @param transformer the response transformer
     */
    public void patch(String path, Route route, ResponseTransformer transformer) {
        addRoute(HttpMethod.patch, ResponseTransformerRouteImpl.create(path, route, transformer));
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
        addRoute(HttpMethod.patch, ResponseTransformerRouteImpl.create(path, acceptType, route, transformer));
    }

    /**
     * Create route implementation or use default response transformer
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      the route
     * @return ResponseTransformerRouteImpl or RouteImpl
     */
    private RouteImpl createRouteImpl(String path, String acceptType, Route route) {
        if (defaultResponseTransformer != null) {
            return ResponseTransformerRouteImpl.create(path, acceptType, route, defaultResponseTransformer);
        }
        return RouteImpl.create(path, acceptType, route);
    }

    /**
     * Create route implementation or use default response transformer
     *
     * @param path  the path
     * @param route the route
     * @return ResponseTransformerRouteImpl or RouteImpl
     */
    private RouteImpl createRouteImpl(String path, Route route) {
        if (defaultResponseTransformer != null) {
            return ResponseTransformerRouteImpl.create(path, route, defaultResponseTransformer);
        }
        return RouteImpl.create(path, route);
    }

    /**
     * Sets default response transformer
     *
     * @param transformer
     */
    public void defaultResponseTransformer(ResponseTransformer transformer) {
        defaultResponseTransformer = transformer;
    }
}
