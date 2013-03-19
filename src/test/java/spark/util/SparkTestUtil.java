package spark.util;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import spark.utils.IOUtils;

public class SparkTestUtil {

    private int port;

    public SparkTestUtil(int port) {
        this.port = port;
    }

    public UrlResponse doMethodSecure(String requestMethod, String path,
            String body) throws Exception {
        return doMethod(requestMethod, path, body, true);
    }

    public UrlResponse doMethod(String requestMethod, String path, String body)
            throws Exception {
        return doMethod(requestMethod, path, body, false);
    }

    private UrlResponse doMethod(String requestMethod, String path,
            String body, boolean secureConnection) throws Exception {
        URL url = new URL((secureConnection ? "https" : "http")
                + "://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (secureConnection) {
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(getSslFactory());
        }

        connection.setRequestMethod(requestMethod);

        if (requestMethod.equals("POST") && body != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        String res = IOUtils.toString(connection.getInputStream());
        UrlResponse response = new UrlResponse();
        response.body = res;
        response.status = connection.getResponseCode();
        response.headers = connection.getHeaderFields();
        return response;
    }

    public int getPort() {
        return port;
    }

    /**
     * Convenience method to use own truststore on SSL Sockets. Will default to
     * the self signed keystore provided in resources, but will respect
     * 
     * -Djavax.net.ssl.keyStore=serverKeys
     * -Djavax.net.ssl.keyStorePassword=password
     * -Djavax.net.ssl.trustStore=serverTrust
     * -Djavax.net.ssl.trustStorePassword=password SSLApplication
     * 
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

            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
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
        String password = System
                .getProperty("javax.net.ssl.trustStorePassword");
        return password == null ? getKeystorePassword() : password;
    }

    public static class UrlResponse {

        public Map<String, List<String>> headers;
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
