package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.After;
import org.junit.Test;

import spark.embeddedserver.EmbeddedServer;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

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
        final Routes routes = mock(Routes.class);

        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(new Server());

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, false);

        embeddedServer.ignite("localhost", 6757, null, 100, 10, 10000);

        verify(jettyServerFactory, times(1)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withThreadPool() throws Exception {
        final QueuedThreadPool threadPool = new QueuedThreadPool(100);
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final Routes routes = mock(Routes.class);

        when(jettyServerFactory.create(threadPool)).thenReturn(new Server(threadPool));

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(threadPool);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, false);

        embeddedServer.ignite("localhost", 6758, null, 0, 0, 0);

        verify(jettyServerFactory, times(1)).create(threadPool);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @Test
    public void create_withNullThreadPool() throws Exception {
        final JettyServerFactory jettyServerFactory = mock(JettyServerFactory.class);
        final StaticFilesConfiguration staticFilesConfiguration = mock(StaticFilesConfiguration.class);
        final Routes routes = mock(Routes.class);

        when(jettyServerFactory.create(100, 10, 10000)).thenReturn(new Server());

        final EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(jettyServerFactory).withThreadPool(null);
        embeddedServer = embeddedJettyFactory.create(routes, staticFilesConfiguration, false);

        embeddedServer.ignite("localhost", 6759, null, 100, 10, 10000);

        verify(jettyServerFactory, times(1)).create(100, 10, 10000);
        verifyNoMoreInteractions(jettyServerFactory);
    }

    @After
    public void tearDown() throws Exception {
        if (embeddedServer != null) {
            embeddedServer.extinguish();
        }
    }
}
