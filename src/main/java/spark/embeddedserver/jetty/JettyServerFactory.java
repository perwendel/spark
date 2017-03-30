package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.Server;

/**
 * This interface can be implemented to provide custom Jetty server instances
 * with specific settings or features.
 */
@FunctionalInterface
public interface JettyServerFactory {
    /**
     * Creates a Jetty server.
     *
     * @param maxThreads          maxThreads
     * @param minThreads          minThreads
     * @param threadTimeoutMillis threadTimeoutMillis
     * @return a new jetty server instance
     */
    Server create(int maxThreads, int minThreads, int threadTimeoutMillis);
}
