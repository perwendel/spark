package spark;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import spark.ssl.SslStores;

/**
 * Created by Per Wendel on 2015-09-01.
 */
public interface SparkServer {

    /**
     * Ignites the spark server, listening on the specified port, running SSL secured with the specified keystore
     * and truststore.  If truststore is null, keystore is reused.
     *
     * @param host                       The address to listen on
     * @param port                       - the port
     * @param sslStores                  - The SSL sslStores.
     * @param latch                      - the countdown latch
     * @param maxThreads                 - max nbr of threads.
     * @param minThreads                 - min nbr of threads.
     * @param threadIdleTimeoutMillis    - idle timeout (ms).
     * @param webSocketHandlers          - web socket handlers.
     * @param webSocketIdleTimeoutMillis - Optional WebSocket idle timeout (ms).
     */
    void ignite(String host,
                int port,
                SslStores sslStores,
                CountDownLatch latch,
                int maxThreads,
                int minThreads,
                int threadIdleTimeoutMillis,
                Map<String, Class<?>> webSocketHandlers,
                Optional<Integer> webSocketIdleTimeoutMillis);

    void stop();
}
