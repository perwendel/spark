package spark.embeddedserver.jetty.websocket;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.server.pathmap.PathMappings;
import org.eclipse.jetty.websocket.server.pathmap.PathSpec;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class WebSocketServletContextHandlerFactoryTest {

    final String webSocketPath = "/websocket";
    private ServletContextHandler servletContextHandler;

    @Test
    public void testCreate_whenWebSocketHandlersIsNull_thenReturnNull() throws Exception {

        servletContextHandler = WebSocketServletContextHandlerFactory.create(null, Optional.empty());

        assertNull("Should return null because no WebSocket Handlers were passed", servletContextHandler);

    }

    @Test
    public void testCreate_whenNoIdleTimeoutIsPresent() throws Exception {

        Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

        webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());

        WebSocketUpgradeFilter webSocketUpgradeFilter =
                (WebSocketUpgradeFilter) servletContextHandler.getAttribute("org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter");

        assertNotNull("Should return a WebSocketUpgradeFilter because we configured it to have one", webSocketUpgradeFilter);

        PathMappings.MappedResource<WebSocketCreator> mappedResource = webSocketUpgradeFilter.getMappings().getMatch("/websocket");
        WebSocketCreatorFactory.SparkWebSocketCreator sc = (WebSocketCreatorFactory.SparkWebSocketCreator) mappedResource.getResource();
        PathSpec pathSpec = (PathSpec) mappedResource.getPathSpec();

        assertEquals("Should return the WebSocket path specified when contexst handler was created",
                webSocketPath, pathSpec.getPathSpec());

        assertTrue("Should return true because handler should be an instance of the one we passed when it was created",
                sc.getHandler() instanceof WebSocketTestHandler);

    }

    @Test
    public void testCreate_whenTimeoutIsPresent() throws Exception {

        final Integer timeout = Integer.valueOf(1000);

        Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

        webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.of(timeout));

        WebSocketUpgradeFilter webSocketUpgradeFilter =
                (WebSocketUpgradeFilter) servletContextHandler.getAttribute("org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter");

        assertNotNull("Should return a WebSocketUpgradeFilter because we configured it to have one", webSocketUpgradeFilter);

        WebSocketServerFactory webSocketServerFactory = webSocketUpgradeFilter.getFactory();
        assertEquals("Timeout value should be the same as the timeout specified when context handler was created",
                timeout.longValue(), webSocketServerFactory.getPolicy().getIdleTimeout());

        PathMappings.MappedResource<WebSocketCreator> mappedResource = webSocketUpgradeFilter.getMappings().getMatch("/websocket");
        WebSocketCreatorFactory.SparkWebSocketCreator sc = (WebSocketCreatorFactory.SparkWebSocketCreator) mappedResource.getResource();
        PathSpec pathSpec = (PathSpec) mappedResource.getPathSpec();

        assertEquals("Should return the WebSocket path specified when context handler was created",
                webSocketPath, pathSpec.getPathSpec());

        assertTrue("Should return true because handler should be an instance of the one we passed when it was created",
                sc.getHandler() instanceof WebSocketTestHandler);
    }

    @Test
    @PrepareForTest(WebSocketServletContextHandlerFactory.class)
    public void testCreate_whenWebSocketContextHandlerCreationFails_thenThrowException() throws Exception {

        PowerMockito.whenNew(ServletContextHandler.class).withAnyArguments().thenThrow(new Exception(""));

        Map<String, WebSocketHandlerWrapper> webSocketHandlers = new HashMap<>();

        webSocketHandlers.put(webSocketPath, new WebSocketHandlerClassWrapper(WebSocketTestHandler.class));

        servletContextHandler = WebSocketServletContextHandlerFactory.create(webSocketHandlers, Optional.empty());

        assertNull("Should return null because Websocket context handler was not created", servletContextHandler);

    }
}