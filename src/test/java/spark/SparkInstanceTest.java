package spark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

import spark.ssl.SslStores;

public class SparkInstanceTest {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int NOT_FOUND_STATUS_CODE = HttpServletResponse.SC_NOT_FOUND;

    private SparkInstance sparkInstance;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void test(){
        sparkInstance = new SparkInstance();
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenOutParameters_thenThrowHaltException(){
        sparkInstance.halt();
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenStatusCode_thenThrowHaltException(){
        sparkInstance.halt(NOT_FOUND_STATUS_CODE);
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenBodyContent_thenThrowHaltException(){
        sparkInstance.halt("error");
    }

    @Test(expected = HaltException.class)
    public void testHalt_whenStatusCodeAndBodyContent_thenThrowHaltException(){
        sparkInstance.halt(NOT_FOUND_STATUS_CODE, "error");
    }

    @Test
    public void testIpAddress_whenInitializedFalse(){
        sparkInstance.ipAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(sparkInstance, "ipAddress");
        assertEquals("IP address should be set to the IP address that was specified", IP_ADDRESS, ipAddress);
    }

    @Test
    public void testIpAddress_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.ipAddress(IP_ADDRESS);
    }

    @Test
    public void testSetIpAddress_whenInitializedFalse(){
        sparkInstance.setIpAddress(IP_ADDRESS);

        String ipAddress = Whitebox.getInternalState(sparkInstance, "ipAddress");
        assertEquals("IP address should be set to the IP address that was specified", IP_ADDRESS, ipAddress);
    }

    @Test
    public void testSetIpAddress_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.setIpAddress(IP_ADDRESS);
    }

    @Test
    public void testPort_whenInitializedFalse(){
        sparkInstance.port(8080);

        int port = Whitebox.getInternalState(sparkInstance, "port");
        assertEquals("Port should be set to the Port that was specified", 8080, port);
    }

    @Test
    public void testPort_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.port(8080);
    }

    @Test
    public void testSetPort_whenInitializedFalse(){
        sparkInstance.setPort(8080);

        int port = Whitebox.getInternalState(sparkInstance, "port");
        assertEquals("Port should be set to the Port that was specified", 8080, port);
    }

    @Test
    public void testSetPort_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.setPort(8080);
    }

    @Test
    public void testThreadPool_whenOnlyMaxThreads(){
        sparkInstance.threadPool(100);
        int maxThreads = Whitebox.getInternalState(sparkInstance, "maxThreads");
        int minThreads = Whitebox.getInternalState(sparkInstance, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(sparkInstance, "threadIdleTimeoutMillis");
        assertEquals("Should return maxThreads specified", 100, maxThreads);
        assertEquals("Should return minThreads specified", -1, minThreads);
        assertEquals("Should return threadIdleTimeoutMillis specified", -1, threadIdleTimeoutMillis);
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters(){
        sparkInstance.threadPool(100, 50, 75);
        int maxThreads = Whitebox.getInternalState(sparkInstance, "maxThreads");
        int minThreads = Whitebox.getInternalState(sparkInstance, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(sparkInstance, "threadIdleTimeoutMillis");
        assertEquals("Should return maxThreads specified", 100, maxThreads);
        assertEquals("Should return minThreads specified", 50, minThreads);
        assertEquals("Should return threadIdleTimeoutMillis specified", 75, threadIdleTimeoutMillis);
    }

    @Test
    public void testThreadPool_whenMaxMinAndTimeoutParameters_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.threadPool(100, 50, 75);
    }

    @Test
    public void testSecure_thenReturnNewSslStores(){
        sparkInstance.secure("keyfile", "keypassword", "truststorefile", "truststorepassword");
        SslStores sslStores = Whitebox.getInternalState(sparkInstance, "sslStores");
        assertNotNull("Should return a SslStores because we configured it to have one", sslStores);
        assertEquals("Should return keystoreFile from SslStores", "keyfile", sslStores.keystoreFile());
        assertEquals("Should return keystorePassword from SslStores", "keypassword", sslStores.keystorePassword());
        assertEquals("Should return trustStoreFile from SslStores", "truststorefile", sslStores.trustStoreFile());
        assertEquals("Should return trustStorePassword from SslStores", "truststorepassword", sslStores.trustStorePassword());
    }

    @Test
    public void testSecure_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.secure(null, null, null, null);
    }

    @Test
    public void testSecure_whenInitializedFalse_thenThrowIllegalArgumentException(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must provide a keystore file to run secured");

        sparkInstance.secure(null, null, null, null);
    }

    @Test
    public void testWebSocketIdleTimeoutMillis_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.webSocketIdleTimeoutMillis(100);
    }

    @Test
    public void testWebSocket_whenInitializedTrue_thenThrowIllegalStateException(){
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This must be done before route mapping has begun");

        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.webSocket("/", ListenerHandler.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void testWebSocket_shouldThrowOnNullHandlerClass() {
        sparkInstance.webSocket("/test", (Class<?>) null);
    }

    @Test(expected = NullPointerException.class)
    public void testWebSocket_shouldThrowOnNullHandler() {
        sparkInstance.webSocket("/test", (Object) null);
    }

    @Test
    public void testWebSocket_shouldAllowHandlerClassImplementingWebSocketListener() {
        sparkInstance.webSocket("/test", ListenerHandler.class);
            assertEquals(1, sparkInstance.webSocketHandlers.size());
    }

    @Test
    public void testWebSocket_shouldAllowHandlerClassAnnotatedWithWebSocket() {
        sparkInstance.webSocket("/test", AnnotatedHandler.class);
            assertEquals(1, sparkInstance.webSocketHandlers.size());
    }

    @Test
    public void testWebSocket_shouldThrowOnIncompatibleHandlerClass() {
        try {
            sparkInstance.webSocket("/test", InvalidHandler.class);
            fail("Handler creation should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(SparkInstance.INVALID_WEBSOCKET_HANDLER_MESSAGE, ex.getMessage());
        }
    }

    static class ListenerHandler extends WebSocketAdapter {
            
    }
    
    @WebSocket
    static class AnnotatedHandler {
            
    }
    
    static class InvalidHandler {
            
    }
}

