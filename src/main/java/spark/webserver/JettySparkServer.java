/*
 * Copyright 2011- Per Wendel
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
package spark.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.SparkServer;
import spark.webserver.jetty.JettyServerFactory;
import spark.webserver.jetty.SocketConnectorFactory;
import spark.ssl.SslStores;
import spark.webserver.websocket.WebSocketServletContextHandlerFactory;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
public class JettySparkServer implements SparkServer {

    private static final int SPARK_DEFAULT_PORT = 4567;
    private static final String NAME = "Spark";

    private Handler handler;
    private Server server;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JettySparkServer(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ignite(String host,
                       int port,
                       SslStores sslStores,
                       CountDownLatch latch,
                       int maxThreads,
                       int minThreads,
                       int threadIdleTimeoutMillis,
                       Map<String, Class<?>> webSocketHandlers,
                       Optional<Integer> webSocketIdleTimeoutMillis) {

        if (port == 0) {
            try (ServerSocket s = new ServerSocket(0)) {
                port = s.getLocalPort();
            } catch (IOException e) {
                logger.error("Could not get first available port (port set to 0), using default: {}", SPARK_DEFAULT_PORT);
                port = SPARK_DEFAULT_PORT;
            }
        }

        server = JettyServerFactory.createServer(maxThreads, minThreads, threadIdleTimeoutMillis);

        ServerConnector connector;

        if (sslStores == null) {
            connector = SocketConnectorFactory.createSocketConnector(server, host, port);
        } else {
            connector = SocketConnectorFactory.createSecureSocketConnector(server, host, port, sslStores);
        }

        server = connector.getServer();
        server.setConnectors(new Connector[] {connector});

        ServletContextHandler webSocketServletContextHandler =
                WebSocketServletContextHandlerFactory.create(webSocketHandlers, webSocketIdleTimeoutMillis);

        // Handle web socket routes
        if (webSocketServletContextHandler == null) {
            server.setHandler(handler);
        } else {
            List<Handler> handlersInList = new ArrayList<>();
            handlersInList.add(handler);

            // WebSocket handler must be the last one
            handlersInList.add(webSocketServletContextHandler);

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(handlersInList.toArray(new Handler[handlersInList.size()]));
            server.setHandler(handlers);
        }

        try {
            logger.info("== {} has ignited ...", NAME);
            logger.info(">> Listening on {}:{}", host, port);

            server.start();
            latch.countDown();
            server.join();
        } catch (Exception e) {
            logger.error("ignite failed", e);
            System.exit(100); // NOSONAR
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        logger.info(">>> {} shutting down ...", NAME);
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            logger.error("stop failed", e);
            System.exit(100); // NOSONAR
        }
        logger.info("done");
    }


}
