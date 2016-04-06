package spark;

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
