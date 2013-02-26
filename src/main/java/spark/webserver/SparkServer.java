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
     * ignites the spark server
     */
    void ignite();
    
    /**
     * Ignites the spark server listening on the provided port
     * 
     * @param port The port to listen on
     */
    void ignite(int port);
    
    /**
     * Ignites the spark server listening on the provided address
     * 
     * @param host The address to listen on
     */
    void ignite(String host);
    
    /**
     * Ignites the spark server listening on the provided address and port
     * 
     * @param host The address to listen on
     * @param port The port to listen on
     */
    void ignite(String host, int port);

    /**
     * Ignites the spark server listening on the provided address and port and serve static files.
     *
     * @param host The address to listen on
     * @param port The port to listen on
     * @param staticFileRoute the route to static files in classPath
     */
    void ignite(String host, int port, String staticFileRoute);

    /**
     * Stops the spark server
     */
	void stop();
}
