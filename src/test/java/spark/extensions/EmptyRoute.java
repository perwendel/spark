package spark.extensions;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author David Åse
 */
@FunctionalInterface
interface EmptyRoute extends Route {

    default Object handle(Request request, Response response) throws Exception {
        return handle();
    }

    Object handle() throws Exception;

}
