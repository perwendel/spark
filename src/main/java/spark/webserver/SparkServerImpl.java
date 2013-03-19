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
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Spark server implementation
 * 
 * @author Per Wendel
 */
class SparkServerImpl implements SparkServer {

    /** The logger. */
    // private static final Logger LOG = Logger.getLogger(Spark.class);

    private static final String NAME = "Spark";
    private Handler handler;
    private Server server;

    public SparkServerImpl(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite() {
        ignite(4567);
    }

    @Override
    public void ignite(int port) {
        ignite("0.0.0.0", port);
    }

    @Override
    public void ignite(String host) {
        ignite(host, 4567);
    }

    @Override
    public void ignite(String host, int port) {
        ignite(host, port, null, null, null, null);
    }

    @Override
    public void ignite(String host, int port, String keystoreFile,
            String keystorePassword, String truststoreFile,
            String truststorePassword) {
        
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

        server.setHandler(handler);

        try {
            System.out.println("== " + NAME + " has ignited ...");
            System.out.println(">> Listening on " + host + ":" + port);

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    @Override
    public void stop() {
        System.out.print(">>> " + NAME + " shutting down...");
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
        System.out.println("done");
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
