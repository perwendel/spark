package spark.sugar;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface ResponseRoute extends Route {

    default Object handle(Request request, Response response) throws Exception {
        return handle(response);
    }

    Object handle(Response response) throws Exception;

}
