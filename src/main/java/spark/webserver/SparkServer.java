/*
 * Copyright 2011- Per Wendel
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
package spark.webserver;

/**
 * 
 *
 * @author Per Wendel
 */
public interface SparkServer {
    
    /**
     * Ignites the spark server, listening on the specified port, running SSL secured with the specified keystore
     * and truststore.  If truststore is null, keystore is reused.
     *
     * @param host The address to listen on
     * @param port - the port
     * @param keystoreFile       - The keystore file location as string
     * @param keystorePassword   - the password for the keystore
     * @param truststoreFile     - the truststore file location as string, leave null to reuse keystore
     * @param truststorePassword - the trust store password
     * @param staticFilesRoute    - the route to static files in classPath
     * @param externalFilesLocation    - the route to static files external to classPath.
     */
    void ignite(
            String host, 
            int port, 
            String keystoreFile, 
            String keystorePassword, 
            String truststoreFile, 
            String truststorePassword,
            String staticFilesRoute,
            String externalFilesLocation);
    
    /**
     * Stops the spark server
     */
	void stop();
}
