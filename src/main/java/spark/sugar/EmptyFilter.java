package spark.sugar;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface EmptyFilter extends Filter {

    default void handle(Request request, Response response) throws Exception {
        handle();
    }

    void handle() throws Exception;

}
