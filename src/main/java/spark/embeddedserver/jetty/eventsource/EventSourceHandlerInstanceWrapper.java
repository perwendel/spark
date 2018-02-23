package spark.embeddedserver.jetty.eventsource;

import static java.util.Objects.requireNonNull;

public class EventSourceHandlerInstanceWrapper implements EventSourceHandlerWrapper {
    final Object handler;

    public EventSourceHandlerInstanceWrapper(Object handler) {
        requireNonNull(handler, "EventSource handler cannot be null");
        EventSourceHandlerWrapper.validateHandlerClass(handler.getClass());
        this.handler = handler;
    }

    @Override
    public Object getHandler() {
        return handler;
    }
}
