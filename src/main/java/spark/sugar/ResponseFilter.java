package spark.sugar;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface ResponseFilter extends Filter {

    default void handle(Request request, Response response) throws Exception {
        handle(response);
    }

    void handle(Response response) throws Exception;

}
