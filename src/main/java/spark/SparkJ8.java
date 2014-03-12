package spark;

import spark.route.HttpMethod;
import spark.utils.Function3;
import spark.utils.Procedure3;

/**
 * Created by JAguililla on 4/03/14.
 */
public class SparkJ8 extends Spark {
    static class HandlerRoute extends Route {
        final Function3<Route, Request, Response, Object> mHandler;

        protected HandlerRoute(String path, Function3<Route, Request, Response, Object> aHandler) {
            super (path, DEFAULT_ACCEPT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerRoute(
            String path, String acceptType, Function3<Route, Request, Response, Object> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public Object handle (Request request, Response response) {
            return mHandler.apply (this, request, response);
        }
    }

    static class HandlerFilter extends Filter {
        final Procedure3<Filter, Request, Response> mHandler;

        protected HandlerFilter(Procedure3<Filter, Request, Response> aHandler) {
            super (DEFAUT_CONTENT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerFilter(String path, Procedure3<Filter, Request, Response> aHandler) {
            super (path, DEFAUT_CONTENT_TYPE);
            mHandler = aHandler;
        }

        protected HandlerFilter(
            String path, String acceptType, Procedure3<Filter, Request, Response> aHandler) {

            super (path, acceptType);
            mHandler = aHandler;
        }

        @Override public void handle (Request request, Response response) {
            mHandler.apply (this, request, response);
        }
    }

    public static synchronized void get(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.get.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void get(
        String aPath, String aAcceptType, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.get.name(), new HandlerRoute (aPath, aAcceptType, aHandler));
    }

    public static synchronized void post(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.post.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void put(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.put.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void patch(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.patch.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void delete(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.delete.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void head(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.head.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void trace(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.trace.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void connect(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.connect.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void options(
        String aPath, Function3<Route, Request, Response, Object> aHandler) {

        addRoute(HttpMethod.options.name(), new HandlerRoute (aPath, aHandler));
    }

    public static synchronized void before(
        Procedure3<Filter, Request, Response> aHandler) {
        addFilter (HttpMethod.before.name (), new HandlerFilter (aHandler));
    }

    public static synchronized void before(
        String aPath, Procedure3<Filter, Request, Response> aHandler) {

        addFilter(HttpMethod.before.name(), new HandlerFilter (aPath, aHandler));
    }

    public static synchronized void before(
        String aPath, String aAcceptType, Procedure3<Filter, Request, Response> aHandler) {

        addFilter(HttpMethod.before.name(), new HandlerFilter (aPath, aAcceptType, aHandler));
    }

    public static synchronized void after(
        Procedure3<Filter, Request, Response> aHandler) {
        addFilter (HttpMethod.after.name (), new HandlerFilter (aHandler));
    }

    public static synchronized void after(
        String aPath, Procedure3<Filter, Request, Response> aHandler) {

        addFilter(HttpMethod.after.name(), new HandlerFilter (aPath, aHandler));
    }

    public static synchronized void after(
        String aPath, String aAcceptType, Procedure3<Filter, Request, Response> aHandler) {

        addFilter(HttpMethod.after.name(), new HandlerFilter (aPath, aAcceptType, aHandler));
    }
}
