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
package spark.embeddedserver.jetty;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.server.session.JDBCSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.jetty.websocket.WebSocketHandlerWrapper;
import spark.embeddedserver.jetty.websocket.WebSocketServletContextHandlerFactory;
import spark.ssl.SslStores;
import spark.utils.StringUtils;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
public class EmbeddedJettyServer implements EmbeddedServer {

    private static final int SPARK_DEFAULT_PORT = 4567;
    private static final String NAME = "Spark";

    private final JettyServerFactory serverFactory;
    private final JettyHandler handler;
    private Server server;

    private String clusterNodeName;
    private int clusterScavengeInterval;
    private String clusterDatastoreDriverClassName;
    private String clusterDatastoreDriverConnectionUrl;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, WebSocketHandlerWrapper> webSocketHandlers;
    private Optional<Integer> webSocketIdleTimeoutMillis;

    public EmbeddedJettyServer(JettyServerFactory serverFactory, JettyHandler handler) {
        this.serverFactory = serverFactory;
        this.handler = handler;
    }

    @Override
    public void configureWebSockets(Map<String, WebSocketHandlerWrapper> webSocketHandlers,
                                    Optional<Integer> webSocketIdleTimeoutMillis) {

        this.webSocketHandlers = webSocketHandlers;
        this.webSocketIdleTimeoutMillis = webSocketIdleTimeoutMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public int ignite(String host,
                      int port,
                      SslStores sslStores,
                      int maxThreads,
                      int minThreads,
                      int threadIdleTimeoutMillis) throws Exception {


        if (port == 0) {
            try (ServerSocket s = new ServerSocket(0)) {
                port = s.getLocalPort();
            } catch (IOException e) {
                logger.error("Could not get first available port (port set to 0), using default: {}", SPARK_DEFAULT_PORT);
                port = SPARK_DEFAULT_PORT;
            }
        }

        server = serverFactory.create(maxThreads, minThreads, threadIdleTimeoutMillis);

        ServerConnector connector;

        if (sslStores == null) {
            connector = SocketConnectorFactory.createSocketConnector(server, host, port);
        } else {
            connector = SocketConnectorFactory.createSecureSocketConnector(server, host, port, sslStores);
        }

        server = connector.getServer();
        server.setConnectors(new Connector[]{connector});

        AbstractSessionManager sessionManager = null;

        if (clusterDatastoreDriverConnectionUrl != null) {
            if (clusterDatastoreDriverConnectionUrl.contains("jdbc")) {
                //
                JDBCSessionIdManager sessionIdManager = new JDBCSessionIdManager(server);
                sessionIdManager.setWorkerName(clusterNodeName);
                sessionIdManager.setDriverInfo(clusterDatastoreDriverClassName, clusterDatastoreDriverConnectionUrl);
                sessionIdManager.setScavengeInterval(clusterScavengeInterval);
                server.setSessionIdManager(sessionIdManager);

                //
                sessionManager = new JDBCSessionManager();
                ((JDBCSessionManager) sessionManager).setSaveInterval(0);
                sessionManager.setSessionIdManager(server.getSessionIdManager());
                SessionHandler sessionHandler = new SessionHandler(sessionManager);
                sessionManager.setSessionHandler(sessionHandler);

                handler.setSessionManager(sessionManager);
            }
        }

        ServletContextHandler webSocketServletContextHandler =
            WebSocketServletContextHandlerFactory.create(webSocketHandlers, webSocketIdleTimeoutMillis);

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/");
        contextHandler.setResourceBase(".");
        contextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
        contextHandler.setHandler(handler);

        List<Handler> handlersInList = new ArrayList<>();
        handlersInList.add(contextHandler);

        // Handle web socket routes
        if (webSocketServletContextHandler != null) {
            handlersInList.add(webSocketServletContextHandler);
        }

        // add all handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(handlersInList.toArray(new Handler[handlersInList.size()]));


        server.setHandler(handlers);

        logger.info("== {} has ignited ...", NAME);
        logger.info(">> Listening on {}:{}", host, port);

        server.start();
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void join() throws InterruptedException {
        server.join();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extinguish() {
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

    @Override
    public int activeThreadCount() {
        if (server == null) {
            return 0;
        }
        return server.getThreadPool().getThreads() - server.getThreadPool().getIdleThreads();
    }

    @Override
    public void configureSessionCluster(String clusterNodeName, String clusterDatastoreDriverClassName, String clusterDatastoreDriverConnectionUrl, int clusterScavengeInterval) {

        this.clusterNodeName = clusterNodeName;
        this.clusterScavengeInterval = clusterScavengeInterval;
        this.clusterDatastoreDriverClassName = clusterDatastoreDriverClassName;
        this.clusterDatastoreDriverConnectionUrl = clusterDatastoreDriverConnectionUrl;


    }
}
