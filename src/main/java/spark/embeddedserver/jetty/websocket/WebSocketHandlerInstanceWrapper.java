package spark.embeddedserver.jetty.websocket;

import static java.util.Objects.requireNonNull;

public class WebSocketHandlerInstanceWrapper implements WebSocketHandlerWrapper {
    
    private final Object handler;
    
    public WebSocketHandlerInstanceWrapper(Object handler) {
        requireNonNull(handler, "WebSocket handler cannot be null");
        WebSocketHandlerWrapper.validateHandlerClass(handler.getClass());
        this.handler = handler;
    }

    @Override
    public Object getHandler() {
        return handler;
    }

}
