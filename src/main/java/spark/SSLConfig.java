package spark;

public class SSLConfig {
    private final String keystoreFile;
    private final String keystorePassword;
    private final String truststoreFile;
    private final String truststorePassword;

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * <p/>
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     */
    public SSLConfig(String keystoreFile, String keystorePassword, String truststoreFile, String truststorePassword) {
        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststoreFile() {
        return truststoreFile;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }
}
