package spark.embeddedserver.jetty.logger;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestLogCreatorFactoryTest {

    @Test
    public void testCreateRequestLogHandler() {
        RequestLogHandler requestLogHandler = RequestLogCreatorFactory.create(new RequestLogClassWrapper(NCSARequestLog.class));
        assertTrue(requestLogHandler instanceof RequestLogHandler);
    }

    @Test
    public void testCannotCreateInvalidHandlers() {
        try {
            RequestLogCreatorFactory.create(new RequestLogClassWrapper(InvalidHandler.class));
            fail("Handler creation should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                    "RequestLog instance must implement 'RequestLog'",
                    ex.getMessage());
        }
    }

    static class InvalidHandler {

    }
}
