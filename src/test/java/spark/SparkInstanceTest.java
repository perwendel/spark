package spark;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import spark.ssl.SslStores;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SparkInstanceTest {

    @Test(expected = HaltException.class)
    public void testHalt_withOutParameters_thenThrowHaltException(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.halt();
    }

    @Test(expected = HaltException.class)
    public void testHalt_withStatusCode_thenThrowHaltException(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.halt(100);
    }

    @Test(expected = HaltException.class)
    public void testHalt_withBodyContent_thenThrowHaltException(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.halt("error");
    }

    @Test(expected = HaltException.class)
    public void testHalt_withStatusCodeAndBodyContent_thenThrowHaltException(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.halt(100, "error");
    }

    @Test
    public void testIpAddress_withInitializedFalse(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.ipAddress("127.0.0.1");

        String ipAddress = Whitebox.getInternalState(sparkInstance, "ipAddress");
        assertEquals("Value is not equal", "127.0.0.1", ipAddress);
    }

    @Test(expected = IllegalStateException.class)
    public void testIpAddress_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.ipAddress("127.0.0.1");
    }

    @Test
    public void testSetIpAddress_withInitializedFalse(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.setIpAddress("127.0.0.1");

        String ipAddress = Whitebox.getInternalState(sparkInstance, "ipAddress");
        assertEquals("Value is not equal", "127.0.0.1", ipAddress);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetIpAddress_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.setIpAddress("127.0.0.1");
    }

    @Test
    public void testPort_withInitializedFalse(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.port(8080);

        int port = Whitebox.getInternalState(sparkInstance, "port");
        assertEquals("Value is not equal", 8080, port);
    }

    @Test(expected = IllegalStateException.class)
    public void testPort_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.port(8080);
    }

    @Test
    public void testSetPort_withInitializedFalse(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.setPort(8080);

        int port = Whitebox.getInternalState(sparkInstance, "port");
        assertEquals("Value is not equal", 8080, port);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetPort_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.setPort(8080);
    }

    @Test
    public void testThreadPool_withOnlyMaxThreads(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.threadPool(100);
        int maxThreads = Whitebox.getInternalState(sparkInstance, "maxThreads");
        int minThreads = Whitebox.getInternalState(sparkInstance, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(sparkInstance, "threadIdleTimeoutMillis");
        assertEquals("Value is not equal", 100, maxThreads);
        assertEquals("Value is not equal", -1, minThreads);
        assertEquals("Value is not equal", -1, threadIdleTimeoutMillis);
    }

    @Test
    public void testThreadPool_withMaxMinAndTimeoutParameters(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.threadPool(100, 50, 75);
        int maxThreads = Whitebox.getInternalState(sparkInstance, "maxThreads");
        int minThreads = Whitebox.getInternalState(sparkInstance, "minThreads");
        int threadIdleTimeoutMillis = Whitebox.getInternalState(sparkInstance, "threadIdleTimeoutMillis");
        assertEquals("Value is not equal", 100, maxThreads);
        assertEquals("Value is not equal", 50, minThreads);
        assertEquals("Value is not equal", 75, threadIdleTimeoutMillis);
    }

    @Test(expected = IllegalStateException.class)
    public void testThreadPool_withMaxMinAndTimeoutParameters_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.threadPool(100, 50, 75);
    }

    @Test
    public void testSecure_thenReturnNewSslStores(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.secure("keyfile", "keypassword", "truststorefile", "truststorepassword");
        SslStores sslStores = Whitebox.getInternalState(sparkInstance, "sslStores");
        assertNotNull("SslStores is null", sslStores);
        assertEquals("keystoreFile is not equal", "keyfile", sslStores.keystoreFile());
        assertEquals("keystorePassword is not equal", "keypassword", sslStores.keystorePassword());
        assertEquals("trustStoreFile is not equal", "truststorefile", sslStores.trustStoreFile());
        assertEquals("trustStorePassword is not equal", "truststorepassword", sslStores.trustStorePassword());
    }

    @Test(expected = IllegalStateException.class)
    public void testSecure_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.secure(null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSecure_withInitializedFalse_thenThrowIllegalArgumentException(){
        SparkInstance sparkInstance = new SparkInstance();
        sparkInstance.secure(null, null, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testWebSocketIdleTimeoutMillis_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.webSocketIdleTimeoutMillis(100);
    }

    @Test(expected = IllegalStateException.class)
    public void testWebSocket_withInitializedTrue_thenThrowIllegalStateException(){
        SparkInstance sparkInstance = new SparkInstance();
        Whitebox.setInternalState(sparkInstance, "initialized", true);
        sparkInstance.webSocket("/", Object.class);
    }
}