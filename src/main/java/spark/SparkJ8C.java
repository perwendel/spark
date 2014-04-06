package spark;

import java.util.function.Consumer;
import java.util.function.Function;

import spark.route.HttpMethod;

/**
 * Created by JAguililla on 4/03/14.
 */
public class SparkJ8C extends SparkJ8 {
    static class HandlerRouteC extends Route {
        final Function<RouteContext, Object> mHandler;

        protected HandlerRouteC (String path, Function<RouteContext, Object> aHandler) {
            super (path, DEFAULT_ACCEPT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerRouteC (
            String path, String acceptType, Function<RouteContext, Object> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public Object handle (Request request, Response response) {
            return mHandler.apply (new RouteContext (this, request, response));
        }
    }

    static class HandlerFilterC extends Filter {
        final Consumer<FilterContext> mHandler;

        protected HandlerFilterC (Consumer<FilterContext> aHandler) {
            super (DEFAUT_CONTENT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerFilterC (String path, Consumer<FilterContext> aHandler) {
            super (path, DEFAUT_CONTENT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerFilterC (
            String path, String acceptType, Consumer<FilterContext> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public void handle (Request request, Response response) {
            mHandler.accept (new FilterContext (this, request, response));
        }
    }

    public static synchronized void get (String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.get.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void get(
        String aPath, String aAcceptType, Function<RouteContext, Object> aHandler) {

        addRoute(HttpMethod.get.name(), new HandlerRouteC (aPath, aAcceptType, aHandler));
    }

    public static synchronized void post(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.post.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void put(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.put.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void patch(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.patch.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void delete(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.delete.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void head(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.head.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void trace(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.trace.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void connect(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.connect.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void options(String aPath, Function<RouteContext, Object> aHandler) {
        addRoute(HttpMethod.options.name(), new HandlerRouteC (aPath, aHandler));
    }

    public static synchronized void before(Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.before.name (), new HandlerFilterC (aHandler));
    }

    public static synchronized void before(String aPath, Consumer<FilterContext> aHandler) {
        addFilter(HttpMethod.before.name(), new HandlerFilterC (aPath, aHandler));
    }

    public static synchronized void before(
        String aPath, String aAcceptType, Consumer<FilterContext> aHandler) {

        addFilter(HttpMethod.before.name(), new HandlerFilterC (aPath, aAcceptType, aHandler));
    }

    public static synchronized void after(Consumer<FilterContext> aHandler) {
        addFilter (HttpMethod.after.name (), new HandlerFilterC (aHandler));
    }

    public static synchronized void after(String aPath, Consumer<FilterContext> aHandler) {
        addFilter(HttpMethod.after.name(), new HandlerFilterC (aPath, aHandler));
    }

    public static synchronized void after(
        String aPath, String aAcceptType, Consumer<FilterContext> aHandler) {

        addFilter(HttpMethod.after.name(), new HandlerFilterC (aPath, aAcceptType, aHandler));
    }
}
