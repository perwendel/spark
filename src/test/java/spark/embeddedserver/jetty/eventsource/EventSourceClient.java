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
    private final Socket socket;
    public EventSourceClient(Socket socket) {
        this.socket = socket;
    }

    public void close() throws IOException, InterruptedException {
        socket.close();
        TimeUnit.SECONDS.sleep(15);
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
            if (line.length() == 0)
                break;
        }
        return reader;
    }
}
