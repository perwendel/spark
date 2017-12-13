package spark.embeddedserver.jetty.websocket;

import javax.annotation.Nonnull;

public class WebSocketHandlerClassWrapper implements WebSocketHandlerWrapper {
    
    private final Class<?> handlerClass;

    public WebSocketHandlerClassWrapper(@Nonnull Class<?> handlerClass) {
        WebSocketHandlerWrapper.validateHandlerClass(handlerClass);
        this.handlerClass = handlerClass;
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
