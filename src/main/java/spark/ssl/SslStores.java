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
package spark.ssl;

/**
 * SSL Stores
 */
public class SslStores {

    protected String keystoreFile;
    protected String keystorePassword;
    protected String certAlias;
    protected String truststoreFile;
    protected String truststorePassword;
    protected boolean needsClientCert;

    /**
     * Creates a Stores instance.
     *
     * @param keystoreFile the keystoreFile
     * @param keystorePassword the keystorePassword
     * @param truststoreFile the truststoreFile
     * @param truststorePassword the truststorePassword
     * @return the SslStores instance.
     */
    public static SslStores create(String keystoreFile,
                                String keystorePassword,
                                String truststoreFile,
                                String truststorePassword) {

        return new SslStores(keystoreFile, keystorePassword, null, truststoreFile, truststorePassword, false);
    }

    public static SslStores create(String keystoreFile,
                                String keystorePassword,
                                String certAlias,
                                String truststoreFile,
                                String truststorePassword) {

        return new SslStores(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword, false);
    }

    public static SslStores create(String keystoreFile,
                                   String keystorePassword,
                                   String truststoreFile,
                                   String truststorePassword,
                                   boolean needsClientCert) {

        return new SslStores(keystoreFile, keystorePassword, null, truststoreFile, truststorePassword, needsClientCert);
    }

    public static SslStores create(String keystoreFile,
                                   String keystorePassword,
                                   String certAlias,
                                   String truststoreFile,
                                   String truststorePassword,
                                   boolean needsClientCert) {

        return new SslStores(keystoreFile, keystorePassword, certAlias, truststoreFile, truststorePassword, needsClientCert);
    }

    private SslStores(String keystoreFile,
                      String keystorePassword,
                      String certAlias,
                      String truststoreFile,
                      String truststorePassword,
                      boolean needsClientCert) {
        this.keystoreFile = keystoreFile;
        this.keystorePassword = keystorePassword;
        this.certAlias = certAlias;
        this.truststoreFile = truststoreFile;
        this.truststorePassword = truststorePassword;
        this.needsClientCert = needsClientCert;
    }

    /**
     * @return keystoreFile
     */
    public String keystoreFile() {
        return keystoreFile;
    }

    /**
     * @return keystorePassword
     */
    public String keystorePassword() {
        return keystorePassword;
    }

    /**
     * @return certAlias
     */
    public String certAlias() {
        return certAlias;
    }

    /**
     * @return trustStoreFile
     */
    public String trustStoreFile() {
        return truststoreFile;
    }

    /**
     * @return trustStorePassword
     */
    public String trustStorePassword() {
        return truststorePassword;
    }

    /**
     * @return needsClientCert
     */
    public boolean needsClientCert() {
        return needsClientCert;
    }
}
