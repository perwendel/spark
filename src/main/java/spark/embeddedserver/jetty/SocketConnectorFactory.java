/*
 * Copyright 2015 - Per Wendel
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

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NegotiatingServerConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import spark.ssl.SslStores;
import spark.utils.Assert;

/**
 * Creates socket connectors.
 */
public class SocketConnectorFactory {

    /**
     * Creates an ordinary, non-secured Jetty server jetty.
     *
     * @param server Jetty server
     * @param host   host
     * @param port   port
     * @return - a server jetty
     */
    public static ServerConnector createSocketConnector(Server server, String host, int port, boolean trustForwardHeaders) {
        Assert.notNull(server, "'server' must not be null");
        Assert.notNull(host, "'host' must not be null");

        HttpConfiguration httpConfiguration = createHttpConfiguration(trustForwardHeaders);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        ServerConnector connector = new ServerConnector(server, httpConnectionFactory);
        initializeConnector(connector, host, port);
        return connector;
    }

    /**
     * Creates a ssl jetty socket jetty. Keystore required, truststore
     * optional. If truststore not specified keystore will be reused.
     *
     * @param server    Jetty server
     * @param sslStores the security sslStores.
     * @param host      host
     * @param port      port
     * @return a ssl socket jetty
     */
    public static ServerConnector createSecureSocketConnector(Server server,
                                                              String host,
                                                              int port,
                                                              SslStores sslStores,
                                                              boolean trustForwardHeaders) {
        Assert.notNull(server, "'server' must not be null");
        Assert.notNull(host, "'host' must not be null");
        Assert.notNull(sslStores, "'sslStores' must not be null");

        SslContextFactory sslContextFactory = createSslContextFactory(sslStores);

        HttpConfiguration httpConfiguration = createHttpConfiguration(trustForwardHeaders);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

        ServerConnector connector = new ServerConnector(server, sslContextFactory, httpConnectionFactory);
        initializeConnector(connector, host, port);
        return connector;
    }

    /**
     * Creates an ordinary, non-secured Jetty http2 server.
     *
     * @param server Jetty server
     * @param host   host
     * @param port   port
     * @return - a server jetty
     */
    public static ServerConnector createHttp2SocketConnector(Server server,
                                                             String host,
                                                             int port,
                                                             boolean trustForwardHeaders) {
        Assert.notNull(server, "'server' must not be null");
        Assert.notNull(host, "'host' must not be null");

        HttpConfiguration httpConfiguration = createHttpConfiguration(trustForwardHeaders);

        HttpConnectionFactory http1 = new HttpConnectionFactory(httpConfiguration);
        HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(httpConfiguration);

        ServerConnector connector = new ServerConnector(server, http1, http2c);
        initializeConnector(connector, host, port);
        return connector;
    }

    /**
     * Creates a ssl http2 jetty socket jetty. Keystore required, truststore
     * optional. If truststore not specified keystore will be reused.
     *
     * @param server    Jetty server
     * @param sslStores the security sslStores.
     * @param host      host
     * @param port      port
     * @return a ssl socket jetty
     */
    public static ServerConnector createSecureHttp2SocketConnector(Server server,
                                                                   String host,
                                                                   int port,
                                                                   SslStores sslStores,
                                                                   boolean trustForwardHeaders) {
        Assert.notNull(server, "'server' must not be null");
        Assert.notNull(host, "'host' must not be null");
        Assert.notNull(sslStores, "'sslStores' must not be null");

        SslContextFactory sslContextFactory = createSslContextFactory(sslStores);
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setUseCipherSuitesOrder(true);

        HttpConfiguration httpConfiguration = createHttpConfiguration(trustForwardHeaders);

        HttpConnectionFactory http1 = new HttpConnectionFactory(httpConfiguration);
        HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(httpConfiguration);
        NegotiatingServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(http1.getProtocol());

        SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

        ServerConnector connector = new ServerConnector(server, ssl, alpn, http2, http1);
        initializeConnector(connector, host, port);
        return connector;
    }

    private static void initializeConnector(ServerConnector connector, String host, int port) {
        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setHost(host);
        connector.setPort(port);
    }

    private static HttpConfiguration createHttpConfiguration(boolean trustForwardHeaders) {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.addCustomizer(new SecureRequestCustomizer());
        if(trustForwardHeaders)
            httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        return httpConfig;
    }

    private static SslContextFactory createSslContextFactory(SslStores sslStores) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

        sslContextFactory.setKeyStorePath(sslStores.keystoreFile());

        if (sslStores.keystorePassword() != null) {
            sslContextFactory.setKeyStorePassword(sslStores.keystorePassword());
        }

        if (sslStores.certAlias() != null) {
            sslContextFactory.setCertAlias(sslStores.certAlias());
        }

        if (sslStores.trustStoreFile() != null) {
            sslContextFactory.setTrustStorePath(sslStores.trustStoreFile());
        }

        if (sslStores.trustStorePassword() != null) {
            sslContextFactory.setTrustStorePassword(sslStores.trustStorePassword());
        }

        if (sslStores.needsClientCert()) {
            sslContextFactory.setNeedClientAuth(true);
            sslContextFactory.setWantClientAuth(true);
        }

        return sslContextFactory;
    }

}
