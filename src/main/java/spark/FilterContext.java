package spark;

/**
 * Created by jam on 3/04/14.
 */
public class FilterContext extends Context {
    public final Filter filter;

    public FilterContext (Filter aFilter, Request aRequest, Response aResponse) {
        super (aRequest, aResponse);
        filter = aFilter;
    }

    public void handle (Request request, Response response) {
        filter.handle (request, response);
    }

    public String getAcceptType () {
        return filter.getAcceptType ();
    }

    public String getPath () {
        return filter.getPath ();
    }
}
