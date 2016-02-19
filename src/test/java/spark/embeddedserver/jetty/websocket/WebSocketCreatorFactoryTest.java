package spark.embeddedserver.jetty.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.junit.Test;

import spark.embeddedserver.jetty.websocket.WebSocketCreatorFactory.SparkWebSocketCreator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WebSocketCreatorFactoryTest {

    @Test
    public void testCreateWebSocketHandler() {
    	Object handler = new Object();
        WebSocketCreator creator = WebSocketCreatorFactory.create(handler);
        assertEquals(handler, SparkWebSocketCreator.class.cast(creator).getHandler());
    }

    @Test
    public void testCannotCreateWithNullHandlers() {
        try {
            WebSocketCreatorFactory.create(null);
            fail("Handler creation should have thrown a NullPointerException");
        } catch (NullPointerException ex) {
            assertEquals(
                    "handler cannot be null",
                    ex.getMessage());
        }
    }

}
