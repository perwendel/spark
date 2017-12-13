package spark.embeddedserver.jetty.websocket;

import javax.annotation.Nonnull;

public class WebSocketHandlerInstanceWrapper implements WebSocketHandlerWrapper {
    
    private final Object handler;
    
    public WebSocketHandlerInstanceWrapper(@Nonnull Object handler) {
        WebSocketHandlerWrapper.validateHandlerClass(handler.getClass());
        this.handler = handler;
    }

    @Override
    public Object getHandler() {
        return handler;
    }

}
