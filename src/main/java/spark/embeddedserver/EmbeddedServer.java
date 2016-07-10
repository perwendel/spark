/*
 * Copyright 2016 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.embeddedserver;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import spark.ssl.SslStores;

/**
 * Represents an embedded server that can be used in Spark. (this is currently Jetty by default).
 */
public interface EmbeddedServer {

    /**
     * Ignites the embedded server, listening on the specified port, running SSL secured with the specified keystore
     * and truststore.  If truststore is null, keystore is reused.
     *
     * @param host                    The address to listen on
     * @param port                    - the port
     * @param sslStores               - The SSL sslStores.
     * @param latch                   - the countdown latch
     * @param maxThreads              - max nbr of threads.
     * @param minThreads              - min nbr of threads.
     * @param threadIdleTimeoutMillis - idle timeout (ms).
     * @param contextPath             - context path.
     */
    void ignite(String host,
                int port,
                SslStores sslStores,
                CountDownLatch latch,
                int maxThreads,
                int minThreads,
                int threadIdleTimeoutMillis,
                String contextPath);

    /**
     * Configures the web sockets for the embedded server.
     *
     * @param webSocketHandlers          - web socket handlers.
     * @param webSocketIdleTimeoutMillis - Optional WebSocket idle timeout (ms).
     */
    default void configureWebSockets(Map<String, Class<?>> webSocketHandlers,
                                     Optional<Integer> webSocketIdleTimeoutMillis) {

        NotSupportedException.raise(getClass().getSimpleName(), "Web Sockets");
    }

    /**
     * Extinguish the embedded server.
     */
    void extinguish();
}
