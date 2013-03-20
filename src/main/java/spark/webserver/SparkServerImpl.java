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

    public SparkServerImpl(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite(String host, int port, String keystoreFile,
            String keystorePassword, String truststoreFile,
            String truststorePassword, String staticFilesRoute) {
        
        ServerConnector connector;
        
        if (keystoreFile == null) {
            connector = createSocketConnector();
        } else {
            connector = createSecureSocketConnector(keystoreFile,
                    keystorePassword, truststoreFile, truststorePassword);
        }

        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setHost(host);
        connector.setPort(port);

        server = connector.getServer();
        server.setConnectors(new Connector[] { connector });

        if (staticFilesRoute == null) {
            server.setHandler(handler);
        } else {
            ResourceHandler resourceHandler = new ResourceHandler();
            Resource staticResources = Resource.newClassPathResource(staticFilesRoute);
            resourceHandler.setBaseResource(staticResources);
            resourceHandler.setWelcomeFiles(new String[] { "index.html" });
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] { handler, resourceHandler });
            server.setHandler(handlers);
        }
        
        
        try {
            System.out.println("== " + NAME + " has ignited ..."); // NOSONAR
            System.out.println(">> Listening on " + host + ":" + port); // NOSONAR

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
            System.exit(100); // NOSONAR
        }
    }

    @Override
    public void stop() {
        System.out.print(">>> " + NAME + " shutting down..."); // NOSONAR
        try {
            server.stop();
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
     * @param truststoreFile the truststore file location as string, leave null to reuse keystore
     * @param truststorePassword the trust store password
     * 
     * @return a secure socket connector
     */
    private ServerConnector createSecureSocketConnector(String keystoreFile,
            String keystorePassword, String truststoreFile,
            String truststorePassword) {

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
    private ServerConnector createSocketConnector() {
        return new ServerConnector(new Server());
    }

}
