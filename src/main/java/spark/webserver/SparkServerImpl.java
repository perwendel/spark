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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
class SparkServerImpl implements SparkServer {
    private static final String NAME = "Spark";
    private Handler handler;
    private Server server;
    private ExecutorService handlerThread;
    private ExecutorService serverThread;
    private String host;
    private int port;

    public SparkServerImpl(Handler handler, ExecutorService executorService) {
        this.handler = handler;
        this.handlerThread = executorService;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite(String host, int port, String keystoreFile,
            String keystorePassword, String truststoreFile,
            String truststorePassword, String staticFilesFolder,
            String externalFilesFolder) {
        this.host = host;
        this.port = port;
        ServerConnector connector;
        if (keystoreFile == null) {
            connector = createSocketConnector();
        } else {
            connector = createSecureSocketConnector(keystoreFile,
                    keystorePassword, truststoreFile, truststorePassword);
        }
        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setSoLingerTime(-1);
        connector.setHost(this.host);
        connector.setPort(this.port);
        server = connector.getServer();
        server.setConnectors(new Connector[] { connector });
        // Handle static file routes
        if (staticFilesFolder == null && externalFilesFolder == null) {
            server.setHandler(handler);
        } else {
            List<Handler> handlersInList = new ArrayList<Handler>();
            handlersInList.add(handler);
            // Set static file location
            setStaticFileLocationIfPresent(staticFilesFolder, handlersInList);
            // Set external static file location
            setExternalStaticFileLocationIfPresent(externalFilesFolder,
                    handlersInList);
            HandlerList handlers = null;
            if (handlerThread == null) {
                handlers = new HandlerList();
            } else {
                handlers = new AsyncHandlerList(handlerThread);
                // remove the async jetty handler from the list of handlers (if
                // it exists) and replace with a normal
                // jetty handler since the handler list will take care of async
                List<Handler> handlersInListCopy = new ArrayList<Handler>();
                int idx = 0;
                for (Iterator<Handler> iterator = handlersInList.iterator(); iterator
                        .hasNext();) {
                    Handler h = iterator.next();
                    if (h instanceof AsyncJettyHandler) {
                        h = new JettyHandler(((AsyncJettyHandler) h).filter);
                    }
                    handlersInListCopy.add(h);
                }
                handlersInList = handlersInListCopy;
            }
            handlers.setHandlers(handlersInList
                    .toArray(new Handler[handlersInList.size()]));
            server.setHandler(handlers);
        }
        System.out.println("== " + NAME + " has ignited ..."); // NOSONAR
        System.out.println(">> Listening on " + host + ":" + port); // NOSONAR
        serverThread = Executors.newSingleThreadExecutor();
        serverThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace(); // NOSONAR
                    System.exit(100); // NOSONAR
                }
            }
        });
    }

    @Override
    public void stop() {
        System.out.print(">>> " + NAME + " shutting down..."); // NOSONAR
        try {
            if (server != null) {
                server.stop();
                System.out.print(">>> " + NAME + " server stopped..."); // NOSONAR
            }
            if (serverThread != null) {
                serverThread.shutdown();
                System.out.print(">>> " + NAME + " server thread shutdown..."); // NOSONAR
            }
            if (handlerThread != null) {
                handlerThread.shutdown();
                System.out.print(">>> " + NAME + " handler thread shutdown..."); // NOSONAR
            }
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
            System.exit(100); // NOSONAR
        }
        System.out.println("done"); // NOSONAR
    }

    /**
     * Creates a secure jetty socket connector. Keystore required, truststore
     * optional. If truststore not specifed keystore will be reused.
     *
     * @param keystoreFile The keystore file location as string
     * @param keystorePassword the password for the keystore
     * @param truststoreFile the truststore file location as string, leave null
     *            to reuse keystore
     * @param truststorePassword the trust store password
     *
     * @return a secure socket connector
     */
    private static ServerConnector createSecureSocketConnector(
            String keystoreFile, String keystorePassword,
            String truststoreFile, String truststorePassword) {
        SslContextFactory sslContextFactory = new SslContextFactory(
                keystoreFile);
        if (keystorePassword != null) {
            sslContextFactory.setKeyStorePassword(keystorePassword);
        }
        if (truststoreFile != null) {
            sslContextFactory.setTrustStorePath(truststoreFile);
        }
        if (truststorePassword != null) {
            sslContextFactory.setTrustStorePassword(truststorePassword);
        }
        return new ServerConnector(new Server(), sslContextFactory);
    }

    /**
     * Creates an ordinary, non-secured Jetty server connector.
     *
     * @return - a server connector
     */
    private static ServerConnector createSocketConnector() {
        return new ServerConnector(new Server());
    }

    /**
     * Sets static file location if present
     */
    private static void setStaticFileLocationIfPresent(String staticFilesRoute,
            List<Handler> handlersInList) {
        if (staticFilesRoute != null) {
            ResourceHandler resourceHandler = new ResourceHandler();
            Resource staticResources = Resource
                    .newClassPathResource(staticFilesRoute);
            resourceHandler.setBaseResource(staticResources);
            resourceHandler.setWelcomeFiles(new String[] { "index.html" });
            handlersInList.add(resourceHandler);
        }
    }

    /**
     * Sets external static file location if present
     */
    private static void setExternalStaticFileLocationIfPresent(
            String externalFilesRoute, List<Handler> handlersInList) {
        if (externalFilesRoute != null) {
            try {
                ResourceHandler externalResourceHandler = new ResourceHandler();
                Resource externalStaticResources = Resource
                        .newResource(new File(externalFilesRoute));
                externalResourceHandler
                        .setBaseResource(externalStaticResources);
                externalResourceHandler
                        .setWelcomeFiles(new String[] { "index.html" });
                handlersInList.add(externalResourceHandler);
            } catch (IOException exception) {
                exception.printStackTrace(); // NOSONAR
                System.err.println("Error during initialize external resource "
                        + externalFilesRoute); // NOSONAR
            }
        }
    }
}
