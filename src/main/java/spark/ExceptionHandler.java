package spark;

/**
 * Created by Per Wendel on 2014-05-10.
 */
@FunctionalInterface
public interface ExceptionHandler {

    /**
     * Invoked when an exception that is mapped to this handler occurs during routing
     *
     * @param exception The exception that was thrown during routing
     * @param request   The request object providing information about the HTTP request
     * @param response  The response object providing functionality for modifying the response
     */
    void handle(Exception exception, Request request, Response response);
}
