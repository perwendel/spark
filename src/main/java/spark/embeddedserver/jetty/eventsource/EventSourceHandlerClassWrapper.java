package spark.embeddedserver.jetty.eventsource;

import static java.util.Objects.requireNonNull;

public class EventSourceHandlerClassWrapper implements EventSourceHandlerWrapper {
    private final Class<?> handlerClass;
    public EventSourceHandlerClassWrapper(Class<?> handlerClass) {
        requireNonNull(handlerClass, "EventSource handler class cannot be null");
        EventSourceHandlerWrapper.validateHandlerClass(handlerClass);
        this.handlerClass = handlerClass;
    }
    @Override
    public Object getHandler() {
        try {
            return handlerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Could not instantiate event source handler", ex);
        }
    }
}
