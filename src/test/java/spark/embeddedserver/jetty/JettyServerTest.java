package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class JettyServerTest {

    @Test
    public void testCreateServer_useDefaults() throws Exception {

        Server server = JettyServer.create(0, 0, 0, false);

        QueuedThreadPool threadPool = (QueuedThreadPool) server.getThreadPool();

        int minThreads = (int) Whitebox.getInternalState(threadPool, "_minThreads");
        int maxThreads = (int) Whitebox.getInternalState(threadPool, "_maxThreads");
        int idleTimeout = (int) Whitebox.getInternalState(threadPool, "_idleTimeout");
        int beanCount = ((Collection) Whitebox.getInternalState(server, "_beans")).size();

        assertEquals("Server thread pool default minThreads should be 8", 8, minThreads);
        assertEquals("Server thread pool default maxThreads should be 200", 200, maxThreads);
        assertEquals("Server thread pool default idleTimeout should be 60000", 60000, idleTimeout);
        assertEquals("There should only be one bean for thread pool", 1, beanCount);
    }



    @Test
    public void testCreateServer_whenNonDefaultMaxThreadOnly_thenUseDefaultMinThreadAndTimeout() throws Exception {

        Server server = JettyServer.create(1, 0, 0, true);

        QueuedThreadPool threadPool = (QueuedThreadPool) server.getThreadPool();

        int minThreads = (int) Whitebox.getInternalState(threadPool, "_minThreads");
        int maxThreads = (int) Whitebox.getInternalState(threadPool, "_maxThreads");
        int idleTimeout = (int) Whitebox.getInternalState(threadPool, "_idleTimeout");
        int beanCount = ((Collection) Whitebox.getInternalState(server, "_beans")).size();

        assertEquals("Server thread pool default minThreads should be 1", 1, minThreads);
        assertEquals("Server thread pool default maxThreads should be the same as specified", 1, maxThreads);
        assertEquals("Server thread pool default idleTimeout should be 60000", 60000, idleTimeout);
        assertEquals("There should be one bean for thread pool and one for jmx", 2, beanCount);

    }
}