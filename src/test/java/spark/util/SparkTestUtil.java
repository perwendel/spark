package spark.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class SparkTestUtil {

    private int port;

    private HttpClient httpClient;

    public SparkTestUtil(int port) {
        this.port = port;
        this.httpClient = httpClientBuilder().build();
    }

    private HttpClientBuilder httpClientBuilder() {
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(getSslFactory(), (paramString, paramSSLSession) -> true);
        Registry<ConnectionSocketFactory> socketRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslConnectionSocketFactory)
                .build();
        BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager(socketRegistry);
        return HttpClientBuilder.create().setConnectionManager(connManager);
    }

    public void setFollowRedirectStrategy(Integer... codes) {
        final List<Integer> redirectCodes = Arrays.asList(codes);
        DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy() {
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
                boolean isRedirect = false;
                try {
                    isRedirect = super.isRedirected(request, response, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isRedirect) {
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (redirectCodes.contains(responseCode)) {
                        return true;
                    }
                }
                return isRedirect;
            }
        };
        this.httpClient = httpClientBuilder().setRedirectStrategy(redirectStrategy).build();
    }

    public UrlResponse get(String path) throws Exception {
        return doMethod("GET", path, null);
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
        return doMethod(requestMethod, path, body, secureConnection, acceptType, null);
    }

    public UrlResponse doMethod(String requestMethod, String path, String body, boolean secureConnection,
                                String acceptType, Map<String, String> reqHeaders) throws IOException {
        HttpUriRequest httpRequest = getHttpRequest(requestMethod, path, body, secureConnection, acceptType, reqHeaders);
        HttpResponse httpResponse = httpClient.execute(httpRequest);

        UrlResponse urlResponse = new UrlResponse();
        urlResponse.status = httpResponse.getStatusLine().getStatusCode();
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            urlResponse.body = EntityUtils.toString(entity);
        } else {
            urlResponse.body = "";
        }
        Map<String, String> headers = new HashMap<>();
        Header[] allHeaders = httpResponse.getAllHeaders();
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }
        urlResponse.headers = headers;
        return urlResponse;
    }

    private HttpUriRequest getHttpRequest(String requestMethod, String path, String body, boolean secureConnection,
                                          String acceptType, Map<String, String> reqHeaders) {
        try {
            String protocol = secureConnection ? "https" : "http";
            String uri = protocol + "://localhost:" + port + path;

            if (requestMethod.equals("GET")) {
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader("Accept", acceptType);
                addHeaders(reqHeaders, httpGet);
                return httpGet;
            }

            if (requestMethod.equals("POST")) {
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setHeader("Accept", acceptType);
                addHeaders(reqHeaders, httpPost);
                httpPost.setEntity(new StringEntity(body));
                return httpPost;
            }

            if (requestMethod.equals("PATCH")) {
                HttpPatch httpPatch = new HttpPatch(uri);
                httpPatch.setHeader("Accept", acceptType);
                addHeaders(reqHeaders, httpPatch);
                httpPatch.setEntity(new StringEntity(body));
                return httpPatch;
            }

            if (requestMethod.equals("DELETE")) {
                HttpDelete httpDelete = new HttpDelete(uri);
                addHeaders(reqHeaders, httpDelete);
                httpDelete.setHeader("Accept", acceptType);
                return httpDelete;
            }

            if (requestMethod.equals("PUT")) {
                HttpPut httpPut = new HttpPut(uri);
                httpPut.setHeader("Accept", acceptType);
                addHeaders(reqHeaders, httpPut);
                httpPut.setEntity(new StringEntity(body));
                return httpPut;
            }

            if (requestMethod.equals("HEAD")) {
                HttpHead httpHead = new HttpHead(uri);
                addHeaders(reqHeaders, httpHead);
                return httpHead;
            }

            if (requestMethod.equals("TRACE")) {
                HttpTrace httpTrace = new HttpTrace(uri);
                addHeaders(reqHeaders, httpTrace);
                return httpTrace;
            }

            if (requestMethod.equals("OPTIONS")) {
                HttpOptions httpOptions = new HttpOptions(uri);
                addHeaders(reqHeaders, httpOptions);
                return httpOptions;
            }

            if (requestMethod.equals("LOCK")) {
                HttpLock httpLock = new HttpLock(uri);
                addHeaders(reqHeaders, httpLock);
                return httpLock;
            }

            throw new IllegalArgumentException("Unknown method " + requestMethod);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHeaders(Map<String, String> reqHeaders, HttpRequest req) {
        if (reqHeaders != null) {
            for (Map.Entry<String, String> header : reqHeaders.entrySet()) {
                req.addHeader(header.getKey(), header.getValue());
            }
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
     * keystore specified in JVM params
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

    static class HttpLock extends HttpRequestBase {
        public final static String METHOD_NAME = "LOCK";

        public HttpLock(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

}
