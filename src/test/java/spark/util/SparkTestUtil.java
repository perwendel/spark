package spark.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class SparkTestUtil {

	private int port;

	private HttpClient httpClient;

	public SparkTestUtil(int port) {
		this.port = port;
		Scheme http = new Scheme("http", port, PlainSocketFactory.getSocketFactory());
		Scheme https = new Scheme("https", port, new org.apache.http.conn.ssl.SSLSocketFactory(getSslFactory(), null));
		SchemeRegistry sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);
		ClientConnectionManager connMrg = new BasicClientConnectionManager(sr);
		this.httpClient = new DefaultHttpClient(connMrg);
	}

	public UrlResponse doMethodSecure(String requestMethod, String path, String body)
			throws Exception {
		return doMethod(requestMethod, path, body, true, "text/html");
	}

	public UrlResponse doMethod(String requestMethod, String path, String body) throws Exception {
		return doMethod(requestMethod, path, body, false, "text/html");
	}

	public UrlResponse doMethodSecure(String requestMethod, String path, String body, String acceptType)
			throws Exception {
		return doMethod(requestMethod, path, body, true, acceptType);
	}

	public UrlResponse doMethod(String requestMethod, String path, String body, String acceptType) throws Exception {
		return doMethod(requestMethod, path, body, false, acceptType);
	}

	private UrlResponse doMethod(String requestMethod, String path, String body, boolean secureConnection,
			String acceptType) throws Exception {

		HttpUriRequest httpRequest = getHttpRequest(requestMethod, path, body, secureConnection, acceptType);
		HttpResponse httpResponse = httpClient.execute(httpRequest);

		UrlResponse urlResponse = new UrlResponse();
		urlResponse.status = httpResponse.getStatusLine().getStatusCode();
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			urlResponse.body = EntityUtils.toString(entity);
		} else {
			urlResponse.body = "";
		}
		Map<String, String> headers = new HashMap<String, String>();
		Header[] allHeaders = httpResponse.getAllHeaders();
		for (Header header : allHeaders) {
			headers.put(header.getName(), header.getValue());
		}
		urlResponse.headers = headers;
		return urlResponse;
	}

	private HttpUriRequest getHttpRequest(String requestMethod, String path, String body, boolean secureConnection,
			String acceptType) {
		try {
			String protocol = secureConnection ? "https" : "http";
			String uri = protocol + "://localhost:" + port + path;

			if (requestMethod.equals("GET")) {
				HttpGet httpGet = new HttpGet(uri);

				httpGet.setHeader("Accept", acceptType);

				return httpGet;
			}

			if (requestMethod.equals("POST")) {
				HttpPost httpPost = new HttpPost(uri);
				httpPost.setHeader("Accept", acceptType);
				httpPost.setEntity(new StringEntity(body));
				return httpPost;
			}

			if (requestMethod.equals("PATCH")) {
				HttpPatch httpPatch = new HttpPatch(uri);
				httpPatch.setHeader("Accept", acceptType);
				httpPatch.setEntity(new StringEntity(body));
				return httpPatch;
			}

			if (requestMethod.equals("DELETE")) {
				HttpDelete httpDelete = new HttpDelete(uri);
				httpDelete.setHeader("Accept", acceptType);
				return httpDelete;
			}

			if (requestMethod.equals("PUT")) {
				HttpPut httpPut = new HttpPut(uri);
				httpPut.setHeader("Accept", acceptType);
				httpPut.setEntity(new StringEntity(body));
				return httpPut;
			}

			if (requestMethod.equals("HEAD")) {
				return new HttpHead(uri);
			}

			if (requestMethod.equals("TRACE")) {
				return new HttpTrace(uri);
			}

			if (requestMethod.equals("OPTIONS")) {
				return new HttpOptions(uri);
			}

			throw new IllegalArgumentException("Unknown method " + requestMethod);

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	public int getPort() {
		return port;
	}

	/**
	 * Convenience method to use own truststore on SSL Sockets. Will default to
	 * the self signed keystore provided in resources, but will respect
	 * <p/>
	 * -Djavax.net.ssl.keyStore=serverKeys
	 * -Djavax.net.ssl.keyStorePassword=password
	 * -Djavax.net.ssl.trustStore=serverTrust
	 * -Djavax.net.ssl.trustStorePassword=password SSLApplication
	 * <p/>
	 * So these can be used to specify other key/trust stores if required.
	 * 
	 * @return an SSL Socket Factory using either provided keystore OR the
	 *         keystore specified in JVM params
	 */
	private SSLSocketFactory getSslFactory() {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			FileInputStream fis = new FileInputStream(getTrustStoreLocation());
			keyStore.load(fis, getTrustStorePassword().toCharArray());
			fis.close();

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
			return ctx.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Return JVM param set keystore or default if not set.
	 * 
	 * @return Keystore location as string
	 */
	public static String getKeyStoreLocation() {
		String keyStoreLoc = System.getProperty("javax.net.ssl.keyStore");
		return keyStoreLoc == null ? "./src/test/resources/keystore.jks" : keyStoreLoc;
	}

	/**
	 * Return JVM param set keystore password or default if not set.
	 * 
	 * @return Keystore password as string
	 */
	public static String getKeystorePassword() {
		String password = System.getProperty("javax.net.ssl.keyStorePassword");
		return password == null ? "password" : password;
	}

	/**
	 * Return JVM param set truststore location, or keystore location if not
	 * set. if keystore not set either, returns default
	 * 
	 * @return truststore location as string
	 */
	public static String getTrustStoreLocation() {
		String trustStoreLoc = System.getProperty("javax.net.ssl.trustStore");
		return trustStoreLoc == null ? getKeyStoreLocation() : trustStoreLoc;
	}

	/**
	 * Return JVM param set truststore password or keystore password if not set.
	 * If still not set, will return default password
	 * 
	 * @return truststore password as string
	 */
	public static String getTrustStorePassword() {
		String password = System.getProperty("javax.net.ssl.trustStorePassword");
		return password == null ? getKeystorePassword() : password;
	}

	public static class UrlResponse {

		public Map<String, String> headers;
		public String body;
		public int status;
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
	}

}
