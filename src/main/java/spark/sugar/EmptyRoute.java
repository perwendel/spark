package spark.sugar;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface EmptyRoute extends Route {

    default Object handle(Request request, Response response) throws Exception {
        return handle();
    }

    Object handle() throws Exception;

}
