package spark.examples.eventsource;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventSourceExample {
    public static void main(String... args){
        Spark.eventSource("/eventsource", EventSourceServletExample.class);
        Spark.init();
    }
    public static class EventSourceServletExample extends EventSourceServlet{
        final Queue<EventSource.Emitter> emitters = new ConcurrentLinkedQueue<>();
        @Override
        protected EventSource newEventSource(HttpServletRequest request) {
            return new EventSource() {
                Emitter emmitter;
                @Override
                public void onOpen(Emitter emitter) throws IOException {
                    this.emmitter = emitter;
                    emitter.data("Event source data message");
                    emitters.add(emitter);
                }

                @Override
                public void onClose() {
                    emitters.remove(this.emmitter);
                    this.emmitter = null;
                }
            };
        }
    }
}
