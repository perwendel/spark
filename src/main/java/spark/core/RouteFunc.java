package spark.core;

import spark.Filter;
import spark.FilterImpl;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.RouteImpl;
import spark.route.HttpMethod;
import spark.utils.SparkUtils;

/**
 * Created by Per Wendel on 2015-09-01.
 */
public interface RouteFunc {

    static final String DEFAULT_ACCEPT_TYPE = "*/*";


    /**
     * Map the route for HTTP GET requests
     *
     * @param path  the path
     * @param route The route
     */
    default void get(final String path, final Route route) {
        addRoute(HttpMethod.get.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path  the path
     * @param route The route
     */
    default void post(String path, Route route) {
        addRoute(HttpMethod.post.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path  the path
     * @param route The route
     */
    default void put(String path, Route route) {
        addRoute(HttpMethod.put.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path  the path
     * @param route The route
     */
    default void patch(String path, Route route) {
        addRoute(HttpMethod.patch.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path  the path
     * @param route The route
     */
    default void delete(String path, Route route) {
        addRoute(HttpMethod.delete.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path  the path
     * @param route The route
     */
    default void head(String path, Route route) {
        addRoute(HttpMethod.head.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path  the path
     * @param route The route
     */
    default void trace(String path, Route route) {
        addRoute(HttpMethod.trace.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path  the path
     * @param route The route
     */
    default void connect(String path, Route route) {
        addRoute(HttpMethod.connect.name(), wrap(path, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path  the path
     * @param route The route
     */
    default void options(String path, Route route) {
        addRoute(HttpMethod.options.name(), wrap(path, route));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    default void before(String path, Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(path, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path   the path
     * @param filter The filter
     */
    default void after(String path, Filter filter) {
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
    default void get(String path, String acceptType, Route route) {
        addRoute(HttpMethod.get.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP POST requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void post(String path, String acceptType, Route route) {
        addRoute(HttpMethod.post.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PUT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void put(String path, String acceptType, Route route) {
        addRoute(HttpMethod.put.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP PATCH requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void patch(String path, String acceptType, Route route) {
        addRoute(HttpMethod.patch.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP DELETE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void delete(String path, String acceptType, Route route) {
        addRoute(HttpMethod.delete.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP HEAD requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void head(String path, String acceptType, Route route) {
        addRoute(HttpMethod.head.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP TRACE requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void trace(String path, String acceptType, Route route) {
        addRoute(HttpMethod.trace.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP CONNECT requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void connect(String path, String acceptType, Route route) {
        addRoute(HttpMethod.connect.name(), wrap(path, acceptType, route));
    }

    /**
     * Map the route for HTTP OPTIONS requests
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      The route
     */
    default void options(String path, String acceptType, Route route) {
        addRoute(HttpMethod.options.name(), wrap(path, acceptType, route));
    }


    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param filter The filter
     */
    default void before(Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param filter The filter
     */
    default void after(Filter filter) {
        addFilter(HttpMethod.after.name(), wrap(SparkUtils.ALL_PATHS, filter));
    }

    /**
     * Maps a filter to be executed before any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    default void before(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.before.name(), wrap(path, acceptType, filter));
    }

    /**
     * Maps a filter to be executed after any matching routes
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     The filter
     */
    default void after(String path, String acceptType, Filter filter) {
        addFilter(HttpMethod.after.name(), wrap(path, acceptType, filter));
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    public static RouteImpl wrap(final String path, final Route route) {
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
    public static RouteImpl wrap(final String path, String acceptType, final Route route) {
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
    public static FilterImpl wrap(final String path, final Filter filter) {
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
    public static FilterImpl wrap(final String path, String acceptType, final Filter filter) {
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

    void addRoute(String httpMethod, RouteImpl route);
    void addFilter(String httpMethod, FilterImpl filter);
    
}
