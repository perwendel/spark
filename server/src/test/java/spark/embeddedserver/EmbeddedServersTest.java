package spark.embeddedserver;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import spark.Spark;
import spark.SparkBaseTest;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.JettyServerFactory;

import java.io.File;

public class EmbeddedServersTest extends SparkBaseTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testAddAndCreate_whenCreate_createsCustomServer() throws Exception {
        // Create custom Server
        Server server = new Server();
        File requestLogDir = temporaryFolder.newFolder();
        File requestLogFile = new File(requestLogDir, "request.log");
        server.setRequestLog(new NCSARequestLog(requestLogFile.getAbsolutePath()));
        JettyServerFactory serverFactory = Mockito.mock(JettyServerFactory.class);
        Mockito.when(serverFactory.create(0, 0, 0)).thenReturn(server);

        String id = "custom";

        // Register custom server
        EmbeddedServers.add(id, new EmbeddedJettyFactory(serverFactory));
        EmbeddedServer embeddedServer = EmbeddedServers.create(id, null, null, null, false);
        Assert.assertNotNull(embeddedServer);

        embeddedServer.trustForwardHeaders(true);
        embeddedServer.ignite("localhost", 0, null, 0, 0, 0);

        Assert.assertTrue(requestLogFile.exists());
        embeddedServer.extinguish();
        Mockito.verify(serverFactory).create(0, 0, 0);
    }

    @Test
    public void testAdd_whenConfigureRoutes_createsCustomServer() throws Exception {
        File requestLogDir = temporaryFolder.newFolder();
        File requestLogFile = new File(requestLogDir, "request.log");
        // Register custom server
        EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new EmbeddedJettyFactory(new JettyServerFactory() {
            @Override
            public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
                Server server = new Server();
                server.setRequestLog(new NCSARequestLog(requestLogFile.getAbsolutePath()));
                return server;
            }

            @Override
            public Server create(ThreadPool threadPool) {
                return null;
            }
        }));
        Spark.get("/", (request, response) -> "OK");
        Spark.awaitInitialization();

        Assert.assertTrue(requestLogFile.exists());
    }

}
