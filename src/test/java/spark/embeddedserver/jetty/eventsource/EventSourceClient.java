package spark.embeddedserver.jetty.eventsource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLSyntaxErrorException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventSourceClient {
    private final CountDownLatch closeLatch;
    private final Socket socket;
    public EventSourceClient(Socket socket) {
        this.socket = socket;
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }
    public void close() throws IOException {
        socket.close();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeLatch.countDown();
    }
    public void writeHTTPRequest(String path) throws IOException {
        int serverPort = socket.getPort();
        final OutputStream output = socket.getOutputStream();
        String handshake = "";
        handshake += "GET " + path + " HTTP/1.1\r\n";
        handshake += "Host: localhost:" + serverPort + "\r\n";
        handshake += "Accept: text/event-stream\r\n";
        handshake += "\r\n";

        output.write(handshake.getBytes("UTF-8"));
        output.flush();
    }

    public BufferedReader readAndDiscardHTTPResponse() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.length() == 0)
                break;
        }
        return reader;
    }
}
