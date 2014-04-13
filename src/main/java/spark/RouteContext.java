package spark;

/**
 * Created by jam on 3/04/14.
 */
public class RouteContext extends Context {
    public final Route route;

    public RouteContext (Route aRoute, Request aRequest, Response aResponse) {
        super (aRoute, aRequest, aResponse);
        route = aRoute;
    }

    public Object handle (Request request, Response response) {
        return route.handle (request, response);
    }

    public String getAcceptType () {
        return route.getAcceptType ();
    }

    public String render (Object element) {
        return route.render (element);
    }

    public String getPath () {
        return route.getPath ();
    }
}
