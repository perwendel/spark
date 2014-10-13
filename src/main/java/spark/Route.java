package spark;

/**
 * Created by Per Wendel on 2014-05-10.
 */
public interface Route {

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return The content to be set in the response
     */
    Object handle(Request request, Response response)  throws Exception;

}
