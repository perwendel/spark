package spark.websocket;

import static java.util.Objects.requireNonNull;

public class WebSocketHandlerClassWrapper implements WebSocketHandlerWrapper {

    private final Class<?> handlerClass;

    public WebSocketHandlerClassWrapper(Class<?> handlerClass) {
        requireNonNull(handlerClass, "WebSocket handler class cannot be null");
        this.handlerClass = handlerClass;
    }

    @Override
    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    @Override
    public Object getHandler() {
        try {
            return handlerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Could not instantiate websocket handler", ex);
        }
    }

}
