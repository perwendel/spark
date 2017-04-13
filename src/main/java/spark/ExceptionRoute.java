package spark;

/**
 * Created by Todd Sharp on 4/13/2017
 */
@FunctionalInterface
public interface ExceptionRoute {

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param exception The exception object (if one exists)
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return The content to be set in the response
     * @throws Exception implementation can choose to throw exception
     */
    Object handle(Exception exception, Request request, Response response) throws Exception;

}
