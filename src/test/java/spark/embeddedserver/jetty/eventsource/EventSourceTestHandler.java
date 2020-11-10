package spark.embeddedserver.jetty.eventsource;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.synchronizedList;

public class EventSourceTestHandler extends EventSourceServlet {
    public static final List<String> events = synchronizedList(new ArrayList<>());
    public static final String ON_CONNECT = "onConnect";
    public static final String ON_CLOSE = "onClose";
    public static final String ES_MESSAGE = "a_message";
    @Override
    protected EventSource newEventSource(HttpServletRequest request) {
        return new EventSource() {
            @Override
            public void onOpen(Emitter emitter) throws IOException {
                events.add(ON_CONNECT);
                events.add(ES_MESSAGE);
                emitter.data(ES_MESSAGE);
            }

            @Override
            public void onClose() {
                events.add(ON_CLOSE);
            }
        };
    }
}
