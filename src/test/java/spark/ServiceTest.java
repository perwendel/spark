package spark;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import spark.embeddedserver.EmbeddedServer;
import spark.route.Routes;
import spark.ssl.SslStores;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static spark.Service.ignite;

public class ServiceTest {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int NOT_FOUND_STATUS_CODE = HttpServletResponse.SC_NOT_FOUND;

    private Service service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void test() {
        service = ignite();
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenOutParameters_thenThrowHaltException() {
        service.halt();
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenStatusCode_thenThrowHaltException() {
        service.halt(NOT_FOUND_STATUS_CODE);
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenBodyContent_thenThrowHaltException() {
        service.halt("error");
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenStatusCodeAndBodyContent_thenThrowHaltException() {
        service.halt(NOT_FOUND_STATUS_CODE, "error");
    }

    @Test
    public void testIpAddress_whenInitializedFalse() {
        service.ipAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(service, "ipAddress");
        assertEquals("IP address should be set to the IP address that was specified", IP_ADDRESS, ipAddress);
    }

    @Test
    public void testIpAddress_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.ipAddress(IP_ADDRESS);
    }

    @Test
    public void testSetIpAddress_whenInitializedFalse() {
        service.ipAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(service, "ipAddress");
        assertEquals("IP address should be set to the IP address that was specified", IP_ADDRESS, ipAddress);
    }

    @Test
    public void testSetIpAddress_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.ipAddress(IP_ADDRESS);
    }

    @Test
    public void testPort_whenInitializedFalse() {
        service.port(8080);

        int port = Whitebox.getInternalState(service, "port");
        assertEquals("Port should be set to the Port that was specified", 8080, port);
    }

    @Test
    public void testPort_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.port(8080);
    }

    @Test
    public void testSetPort_whenInitializedFalse() {
        service.port(8080);

        int port = Whitebox.getInternalState(service, "port");
        assertEquals("Port should be set to the Port that was specified", 8080, port);
    }

    @Test
    public void testSetPort_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.port(8080);
    }

    @Test
    public void testGetPort_whenInitializedFalse_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done after route mapping has begun");

        Whitebox.setInternalState(service, "initialized", false);
        service.port();
    }

    @Test
    public void testGetPort_whenInitializedTrue() {
        int expectedPort = 8080;
        Whitebox.setInternalState(service, "initialized", true);
        Whitebox.setInternalState(service, "port", expectedPort);

        int actualPort = service.port();

        assertEquals("Port retrieved should be the port setted", expectedPort, actualPort);
    }

    @Test
    public void testGetPort_whenInitializedTrue_Default() {
        int expectedPort = Service.SPARK_DEFAULT_PORT;
        Whitebox.setInternalState(service, "initialized", true);

        int actualPort = service.port();

        assertEquals("Port retrieved should be the port setted", expectedPort, actualPort);
    }

    @Test
    public void testThreadPool_whenOnlyMaxThreads() {
        service.threadPool(100);
        int maxThreads = Whitebox.getInternalState(service, "maxThreads");
        int minThreads = Whitebox.getInternalState(service, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(service, "threadIdleTimeoutMillis");
        assertEquals("Should return maxThreads specified", 100, maxThreads);
        assertEquals("Should return minThreads specified", -1, minThreads);
        assertEquals("Should return threadIdleTimeoutMillis specified", -1, threadIdleTimeoutMillis);
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters() {
        service.threadPool(100, 50, 75);
        int maxThreads = Whitebox.getInternalState(service, "maxThreads");
        int minThreads = Whitebox.getInternalState(service, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(service, "threadIdleTimeoutMillis");
        assertEquals("Should return maxThreads specified", 100, maxThreads);
        assertEquals("Should return minThreads specified", 50, minThreads);
        assertEquals("Should return threadIdleTimeoutMillis specified", 75, threadIdleTimeoutMillis);
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.threadPool(100, 50, 75);
    }

    @Test
    public void testSecure_thenReturnNewSslStores() {
        service.secure("keyfile", "keypassword", "truststorefile", "truststorepassword");
        SslStores sslStores = Whitebox.getInternalState(service, "sslStores");
        assertNotNull("Should return a SslStores because we configured it to have one", sslStores);
        assertEquals("Should return keystoreFile from SslStores", "keyfile", sslStores.keystoreFile());
        assertEquals("Should return keystorePassword from SslStores", "keypassword", sslStores.keystorePassword());
        assertEquals("Should return trustStoreFile from SslStores", "truststorefile", sslStores.trustStoreFile());
        assertEquals("Should return trustStorePassword from SslStores", "truststorepassword", sslStores.trustStorePassword());
    }

    @Test
    public void testSecure_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.secure(null, null, null, null);
    }

    @Test
    public void testSecure_whenInitializedFalse_thenThrowIllegalArgumentException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must provide a keystore file to run secured");

        service.secure(null, null, null, null);
    }

    @Test
    public void testWebSocketIdleTimeoutMillis_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.webSocketIdleTimeoutMillis(100);
    }

    @Test
    public void testWebSocket_whenInitializedTrue_thenThrowIllegalStateException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(service, "initialized", true);
        service.webSocket("/", DummyWebSocketListener.class);
    }
    
    @Test
    public void testWebSocket_whenPathNull_thenThrowNullPointerException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("WebSocket path cannot be null");
        service.webSocket(null, new DummyWebSocketListener());
    }
    
    @Test
    public void testWebSocket_whenHandlerNull_thenThrowNullPointerException() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("WebSocket handler class cannot be null");
        service.webSocket("/", null);
    }
    
    @Test(timeout = 300)
    public void stopExtinguishesServer() {
        Service service = Service.ignite();
        Routes routes = Mockito.mock(Routes.class);
        EmbeddedServer server = Mockito.mock(EmbeddedServer.class);
        service.routes = routes;
        service.server = server;
        service.initialized = true;
        service.stop();
        try {
        	// yes, this is ugly and forces to set a test timeout as a precaution :(
            while (service.initialized) {
            	Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Mockito.verify(server).extinguish();
    }
    
    @Test
    public void awaitStopBlocksUntilExtinguished() {
        Service service = Service.ignite();
        Routes routes = Mockito.mock(Routes.class);
        EmbeddedServer server = Mockito.mock(EmbeddedServer.class);
        service.routes = routes;
        service.server = server;
        service.initialized = true;
        service.stop();
        service.awaitStop();
        Mockito.verify(server).extinguish();
        assertFalse(service.initialized);
    }
    
    @WebSocket
    protected static class DummyWebSocketListener {
    }
}
