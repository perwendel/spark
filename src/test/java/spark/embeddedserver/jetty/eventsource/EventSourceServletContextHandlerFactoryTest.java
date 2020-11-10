package spark.embeddedserver.jetty.eventsource;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
public class EventSourceServletContextHandlerFactoryTest {
    final String eventSourcePath = "/eventsource";
    private ServletContextHandler servletContextHandler;

    @Test
    public void testCreate_whenEventSourceHandlersIsNull_thenReturnNull() throws Exception {

        servletContextHandler = EventSourceServletContextHandlerFactory.create(null);

        assertNull("Should return null because no EventSource Handlers were passed", servletContextHandler);

    }

    @Test
    @PrepareForTest(EventSourceServletContextHandlerFactory.class)
    public void testCreate_whenEventSourceContextHandlerCreationFails_thenThrowException() throws Exception {

        PowerMockito.whenNew(ServletContextHandler.class).withAnyArguments().thenThrow(new Exception(""));

        Map<String, EventSourceHandlerWrapper> eventSourceHandlers = new HashMap<>();

        eventSourceHandlers.put(eventSourcePath, new EventSourceHandlerClassWrapper(EventSourceTestHandler.class));

        servletContextHandler = EventSourceServletContextHandlerFactory.create(eventSourceHandlers);

        assertNull("Should return null because EventSource context handler was not created", servletContextHandler);

    }
}
