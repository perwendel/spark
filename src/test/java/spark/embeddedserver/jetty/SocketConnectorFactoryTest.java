package spark.embeddedserver.jetty;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;
import spark.ssl.SslStores;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SocketConnectorFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Server server;

    @Before
    public void before_each_test() {
        server = new Server();
    }

    @Test
    public void testCreateSocketConnector_whenServerIsNull_thenThrowException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'server' must not be null");

        SocketConnectorFactory.createSocketConnector(null, "host", 80, 8192);
    }

    @Test
    public void testCreateSocketConnector_whenHostIsNull_thenThrowException() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'host' must not be null");

        SocketConnectorFactory.createSocketConnector(server, null, 80, 8192);
    }

    @Test
    public void testCreateSocketConnector() {

        final String host = "localhost";
        final int port = 8888;

        final ServerConnector serverConnector = SocketConnectorFactory.createSocketConnector(server, "localhost", 8888, 8192);

        final String internalHost = Whitebox.getInternalState(serverConnector, "_host");
        final int internalPort = Whitebox.getInternalState(serverConnector, "_port");
        final Server internalServerConnector = Whitebox.getInternalState(serverConnector, "_server");

        final Map<String, ConnectionFactory> factories = Whitebox.getInternalState(serverConnector, "_factories");

        assertTrue("Should have HTTP 1.1 connection factory set",
                factories.containsKey("http/1.1") && factories.get("http/1.1") != null);

        assertEquals("Server Connector Host should be set to the specified server", host, internalHost);
        assertEquals("Server Connector Port should be set to the specified port", port, internalPort);
        assertEquals("Server Connector Server should be set to the specified server", internalServerConnector, server);
    }

    @Test
    public void testCreateSecureSocketConnector_whenServerIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'server' must not be null");

        SocketConnectorFactory.createSecureSocketConnector(null, "localhost", 80, null, 8192);
    }

    @Test
    public void testCreateSecureSocketConnector_whenHostIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'host' must not be null");

        SocketConnectorFactory.createSecureSocketConnector(server, null, 80, null, 8192);
    }

    @Test
    public void testCreateSecureSocketConnector_whenSslStoresIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'sslStores' must not be null");

        SocketConnectorFactory.createSecureSocketConnector(server, "localhost", 80, null, 8192);
    }

    @Test
    public void testCreateSecureSocketConnector() throws  Exception {

        final String host = "localhost";
        final int port = 8888;

        final String keystoreFile = "keystoreFile.jks";
        final String keystorePassword = "keystorePassword";
        final String truststoreFile = "truststoreFile.jks";
        final String trustStorePassword = "trustStorePassword";

        final SslStores sslStores = SslStores.create(keystoreFile, keystorePassword, truststoreFile, trustStorePassword);

        final ServerConnector serverConnector = SocketConnectorFactory.createSecureSocketConnector(server, host, port, sslStores, 8192);

        final String internalHost = Whitebox.getInternalState(serverConnector, "_host");
        final int internalPort = Whitebox.getInternalState(serverConnector, "_port");

        assertEquals("Server Connector Host should be set to the specified server", host, internalHost);
        assertEquals("Server Connector Port should be set to the specified port", port, internalPort);

        final Map<String, ConnectionFactory> factories = Whitebox.getInternalState(serverConnector, "_factories");

        assertTrue("Should return true because factory for SSL should have been set",
                factories.containsKey("ssl") && factories.get("ssl") != null);
        assertTrue("Should have HTTP 1.1 connection factory set",
                factories.containsKey("http/1.1") && factories.get("http/1.1") != null);

        final SslConnectionFactory sslConnectionFactory = (SslConnectionFactory) factories.get("ssl");
        final SslContextFactory sslContextFactory = sslConnectionFactory.getSslContextFactory();

        assertEquals("Should return the Keystore file specified", keystoreFile,
                sslContextFactory.getKeyStoreResource().getFile().getName());

        assertEquals("Should return the Truststore file specified", truststoreFile,
                sslContextFactory.getTrustStoreResource().getFile().getName());
    }

}