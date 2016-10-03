package spark.websocket;

/**
 * A wrapper for web socket handler classes/instances.
 */
public interface WebSocketHandlerWrapper {

    /**
     * Gets the handler class.
     * 
     * @return The handler class.
     */
    Class<?> getHandlerClass();

    /**
     * Gets the actual handler - if necessary, instantiating an object.
     * 
     * @return The handler instance.
     */
    Object getHandler();

}
