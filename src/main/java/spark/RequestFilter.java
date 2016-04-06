package spark;

/**
 * @author David Åse
 */
@FunctionalInterface
public interface RequestFilter extends Filter {

    default void handle(Request request, Response response) throws Exception {
        handle(request);
    }

    void handle(Request request) throws Exception;

}
