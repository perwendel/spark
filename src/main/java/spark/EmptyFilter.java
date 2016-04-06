package spark;

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
