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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
class SparkServerImpl implements SparkServer {

    private static final int DEFAULT_SYSTEM_ERROR_CODE = 100;
	private static final int SO_LINGER_TIME = -1;
	private static final int MAX_IDLE_TIME = 1000 * 60 * 60;
	private static final int DEFAULT_PORT = 4567;

	/** The logger. */
    // private static final Logger LOG = Logger.getLogger(Spark.class);
    
    private static final String NAME = "Spark";
    private Handler handler;
	private Server server;

    public SparkServerImpl(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite() {
        ignite(DEFAULT_PORT);
    }

    @Override
    public void ignite(int port) {
        server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(MAX_IDLE_TIME);
        connector.setSoLingerTime(SO_LINGER_TIME);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        server.setHandler(handler);

        try {
            System.out.println("== " + NAME + " has ignited ...");
            System.out.println(">> Listening on 0.0.0.0:" + port);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(DEFAULT_SYSTEM_ERROR_CODE);
        }
    }
    
    @Override
    public void shutdown() {
        System.out.println(">>> " + NAME + " shutting down...");
        try {
			server.stop();
	        server.join();
		} catch (Exception e) {
			e.printStackTrace();
            System.exit(DEFAULT_SYSTEM_ERROR_CODE);
		}
    }
}
