package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class JettyServerTest {

    @Test
    public void testCreateServer_useDefaults() {
        Server server = new JettyServer().create(0, 0, 0);

        QueuedThreadPool threadPool = (QueuedThreadPool) server.getThreadPool();

        int minThreads = Whitebox.getInternalState(threadPool, "_minThreads");
        int maxThreads = Whitebox.getInternalState(threadPool, "_maxThreads");
        int idleTimeout = Whitebox.getInternalState(threadPool, "_idleTimeout");

        Assert.assertEquals("Server thread pool default minThreads should be 8", 8, minThreads);
        Assert.assertEquals("Server thread pool default maxThreads should be 200", 200, maxThreads);
        Assert.assertEquals("Server thread pool default idleTimeout should be 60000", 60000, idleTimeout);
    }

    @Test
    public void testCreateServer_whenNonDefaultMaxThreadsOnly_thenUseDefaultMinThreadsAndTimeout() {
        Server server = new JettyServer().create(9, 0, 0);

        QueuedThreadPool threadPool = (QueuedThreadPool) server.getThreadPool();

        int minThreads = Whitebox.getInternalState(threadPool, "_minThreads");
        int maxThreads = Whitebox.getInternalState(threadPool, "_maxThreads");
        int idleTimeout = Whitebox.getInternalState(threadPool, "_idleTimeout");

        Assert.assertEquals("Server thread pool default minThreads should be 8", 8, minThreads);
        Assert.assertEquals("Server thread pool default maxThreads should be the same as specified", 9, maxThreads);
        Assert.assertEquals("Server thread pool default idleTimeout should be 60000", 60000, idleTimeout);

    }

    @Test
    public void testCreateServer_whenNonDefaultMaxThreads_isLessThanDefaultMinThreads() {
        try {
            new JettyServer().create(2, 0, 0);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("max threads (2) less than min threads (8)", expected.getMessage());
        }
    }
}
