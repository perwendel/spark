package spark.embeddedserver.jetty.eventsource;

import org.eclipse.jetty.servlets.EventSourceServlet;

/**
 * A wrapper for event source handler classes/instances.
 */
public interface EventSourceHandlerWrapper {
    /**
     * Gets the actual handler - if necessary, instantiating an object.
     *
     * @return The handler instance.
     */
    Object getHandler();

    static void validateHandlerClass(Class<?> handlerClass) {
        boolean valid = EventSourceServlet.class.isAssignableFrom(handlerClass);
        if (!valid) {
            throw new IllegalArgumentException(
                "EventSource handler must extend 'EventSourceServlet'");
        }
    }
}
