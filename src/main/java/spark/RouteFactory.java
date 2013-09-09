package spark;

public class RouteFactory extends Route {
    private static final String DEFAULT_ACCEPT_TYPE = "*/*";

    private Class<? extends Route> route;

    /**
     * Constructor
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     */
    protected RouteFactory(String path, Class<? extends Route> route) {
        this(path, DEFAULT_ACCEPT_TYPE, route);

    }

    /**
     * Constructor
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     * @param acceptType The accept type which is used for matching.
     */
    protected RouteFactory(String path, String acceptType,
            Class<? extends Route> route) {
        super(path, acceptType);
        this.route = route;
    }

    @Override
    public Object handle(Request request, Response response) {

        try {
            return route.newInstance().handle(request, response);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Problem instantiating route", e);
        }
    }

}
