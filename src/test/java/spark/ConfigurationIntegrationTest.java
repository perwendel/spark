package spark;

import static org.junit.Assert.*;

import java.util.Locale;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class ConfigurationIntegrationTest {

	private static SparkTestUtil testUtil;
	
	@Test
	public void sendServerVersion() throws Exception {
		startServer(null);
		UrlResponse response = testUtil.doMethod("GET", "/locale", null);
		assertEquals("Jetty(9.0.2.v20130417)", response.headers.get("Server").toString());
		stopServer();
	}
	
	@Test
	public void dontSendServerVersion() throws Exception {
		HttpConfiguration httpConfiguration = new HttpConfiguration();
		httpConfiguration.setSendServerVersion(false);
		HttpConnectionFactory factory = new HttpConnectionFactory(httpConfiguration);
		
		startServer(factory);
		UrlResponse response = testUtil.doMethod("GET", "/locale", null);
		assertNull(response.headers.get("Server"));
		stopServer();
	}
	
    public void stopServer() {
        Spark.stop();
    }

    public void startServer(HttpConnectionFactory factory) throws Exception {
        testUtil = new SparkTestUtil(4567);
        
        if(factory != null) 
        	Spark.connectionFactory(factory);
        
        Spark.get("/locale", (request, response) -> {
            return Locale.ROOT.getCountry();
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
    }

}
