package spark;


@FunctionalInterface
public interface ResponseTransformerExtend {

    /**
     * Method called for rendering the output.
     *
     * @param model object used to render output.
     * @param req request object to render output.
     * @param res response object to render output.
     * @return message that it is sent to client.
     * @throws java.lang.Exception when render fails
     */
    String render(Object model, Request req, Response res) throws Exception;

}
