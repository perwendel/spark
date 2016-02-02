package spark.embeddedserver.jetty.websocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketTestClient {
    private final CountDownLatch closeLatch;

    public WebSocketTestClient() {
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        closeLatch.countDown();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException{
	session.getRemote().sendString("Hi Spark!");
	session.close(StatusCode.NORMAL, "Bye!");
    }
}
