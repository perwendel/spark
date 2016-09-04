package spark.websocket;

import static java.util.Objects.requireNonNull;

public class WebSocketHandlerInstanceWrapper implements WebSocketHandlerWrapper {
    
    private final Object handler;
    
    public WebSocketHandlerInstanceWrapper(Object handler) {
        requireNonNull(handler, "WebSocket handler cannot be null");
        this.handler = handler;
    }
    
    @Override
    public Class<?> getHandlerClass() {
    	return handler.getClass();
    }

    @Override
    public Object getHandler() {
        return handler;
    }

}
