package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.junit.Test;

public class SparkInstanceTest {

	private SparkInstance instance = new SparkInstance();

	@Test(expected = NullPointerException.class)
	public void webSocketShouldThrowOnNullHandlerClass() {
		instance.webSocket("/test", (Class<?>) null);
	}

	@Test(expected = NullPointerException.class)
	public void webSocketShouldThrowOnNullHandler() {
		instance.webSocket("/test", (Object) null);
	}

	@Test
	public void webSocketShouldAllowHandlerClassImplementingWebSocketListener() {
		instance.webSocket("/test", ListenerHandler.class);
		assertEquals(1, instance.webSocketHandlers.size());
	}

	@Test
	public void webSocketShouldAllowHandlerClassAnnotatedWithWebSocket() {
		instance.webSocket("/test", AnnotatedHandler.class);
		assertEquals(1, instance.webSocketHandlers.size());
	}

	@Test
	public void webSocketShouldThrowOnIncompatibleHandlerClass() {
		try {
			instance.webSocket("/test", InvalidHandler.class);
			fail("Handler creation should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			assertEquals(SparkInstance.INVALID_WEBSOCKET_HANDLER_MESSAGE, ex.getMessage());
		}
	}

	static class ListenerHandler extends WebSocketAdapter {
		
	}
	
	@WebSocket
	static class AnnotatedHandler {
		
	}
	
	static class InvalidHandler {
		
	}

}
