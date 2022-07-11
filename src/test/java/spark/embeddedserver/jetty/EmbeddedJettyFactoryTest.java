package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class EmbeddedJettyFactoryTest {

    private EmbeddedServer embeddedServer;
    private JettyServerFactory jettyServerFactory;
    private StaticFilesConfiguration staticFilesConfiguration;
    private ExceptionMapper exceptionMapper;
    private Routes routes;
    private Server server;

    @Before
    public void setUpMocks() {
        jettyServerFactory = mock(JettyServerFactory.class);
        staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        exceptionMapper = mock(ExceptionMapper.class);
        routes = mock(Routes.class);
        server = new Server();
        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(server);
    }

    @Test
    public void create() throws Exception {
        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        igniteServer(embeddedServer, false);
        embeddedServer.extinguish();
        igniteServer(embeddedServer, true);

        verify(jettyServerFactory, times(2)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
        assertTrue(((JettyHandler) server.getHandler()).getSessionCookieConfig().isHttpOnly());
    }

    @Test
    public void create_withThreadPool() throws Exception {
        final QueuedThreadPool threadPool = new QueuedThreadPool(100);

        when(jettyServerFactory.create(threadPool)).thenReturn(new Server(threadPool));

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(threadPool);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        igniteServer(embeddedServer, false);
        embeddedServer.extinguish();
        igniteServer(embeddedServer, true);

        verify(jettyServerFactory, times(2)).create(threadPool);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withNullThreadPool() throws Exception {
        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(null);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, exceptionMapper, false);

        igniteServer(embeddedServer, false);
        embeddedServer.extinguish();
        igniteServer(embeddedServer, true);

        verify(jettyServerFactory, times(2)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withoutHttpOnly() throws Exception {
        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withHttpOnly(false);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, false);

        igniteServer(embeddedServer, false);
        embeddedServer.extinguish();
        igniteServer(embeddedServer, true);

        assertFalse(((JettyHandler) server.getHandler()).getSessionCookieConfig().isHttpOnly());
    }

    @After
    public void tearDown() {
        if (embeddedServer != null) {
            embeddedServer.extinguish();
        }
    }

    private void igniteServer(EmbeddedServer embeddedServer, boolean http2Enabled) throws Exception {
        embeddedServer.trustForwardHeaders(true);
        embeddedServer.ignite("localhost", 6759, null, 100, 10, 10000, http2Enabled);
    }
}
