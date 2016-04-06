package spark;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface RequestRoute extends Route {

    default Object handle(Request request, Response response) throws Exception {
        return handle(request);
    }

    Object handle(Request request) throws Exception;

}
