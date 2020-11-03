package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.After;
import org.junit.Test;

import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class EmbeddedJettyFactoryTest {

    private EmbeddedServer embeddedServer;

    @Test
    public void create() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        Server server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        embeddedServer.ignite("localhost", 6757, null, 100, 10, 10000);

        verify(jettyServerFactory, times(1)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
        assertTrue(((JettyHandler) server.getHandler()).getSessionCookieConfig().isHttpOnly());
    }

    @Test
    public void create_withThreadPool() throws Exception {
        final QueuedThreadPool threadPool = new QueuedThreadPool(100);
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        when(jettyServerFactory.create(threadPool)).thenReturn(new Server(threadPool));

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(threadPool);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        embeddedServer.ignite("localhost", 6758, null, 0, 0, 0);

        verify(jettyServerFactory, times(1)).create(threadPool);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withNullThreadPool() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(new Server());

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(null);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        embeddedServer.ignite("localhost", 6759, null, 100, 10, 10000);

        verify(jettyServerFactory, times(1)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withoutHttpOnly() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        Server server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withHttpOnly(false);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6760, null, 100, 10, 10000);

        assertFalse(((JettyHandler) server.getHandler()).getSessionCookieConfig().isHttpOnly());
    }

    @Test
    public void create_withAdditionalHandler() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);
        final Handler handler1 = mock(Handler.class);
        final Handler handler2 = mock(Handler.class);

        Server server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory)
            .withHandlerBeforeRouteHandler(handler1)
            .withHandlerBeforeRouteHandler(handler2);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6761, null, 100, 10, 10000);

        final Handler actual = server.getHandler();
        assertTrue(actual instanceof HandlerList);
        final Handler[] handlers = ((HandlerList) actual).getHandlers();
        assertEquals(3, handlers.length);
        assertEquals(handler1, handlers[0]);
        assertEquals(handler2, handlers[1]);
        assertTrue(handlers[2] instanceof JettyHandler);
    }

    @Test
    public void create_withServerAttribute() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        Server server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withJettyServerAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", 500);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6762, null, 100, 10, 10000);

        assertEquals(500, server.getAttribute("org.eclipse.jetty.server.Request.maxFormContentSize"));
    }

    @Test
    public void create_withConfigurator() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final ExceptionMapper exceptionMapper = mock(ExceptionMapper.class);
        final Routes routes = mock(Routes.class);

        Server server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);

        final List<Server> configuredServers = new ArrayList<>();
        Consumer<Server> configurator = configuredServers::add;

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withJettyServerConfigurator(configurator);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);
        embeddedServer.ignite("localhost", 6763, null, 100, 10, 10000);

        assertEquals(1, configuredServers.size());
        assertEquals(server, configuredServers.get(0));
    }

    @After
    public void tearDown() {
        if (embeddedServer != null) {
            embeddedServer.extinguish();
        }
    }
}
