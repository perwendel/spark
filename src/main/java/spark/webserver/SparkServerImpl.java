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
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
class SparkServerImpl implements SparkServer {

    /** The logger. */
    // private static final Logger LOG = Logger.getLogger(Spark.class);

    private static final String NAME = "Spark";
    private Handler handler;
    private Server server = new Server();

    public SparkServerImpl(Handler handler) {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite() {
        ignite(4567);
    }
    
    @Override
    public void ignite(int port) {
        ignite("0.0.0.0", port);
    }
    
    @Override
    public void ignite(String host) {
        ignite(host, 4567);
    }

    @Override
    public void ignite(String host, int port) {
        ignite(host, port, null);
    }

    @Override
    public void ignite(String host, int port, String staticFilesRoute) {
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setHost(host);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        if (staticFilesRoute == null) {
            server.setHandler(handler);
        } else {
            ResourceHandler resourceHandler = new ResourceHandler();
            Resource staticResources = Resource.newClassPathResource(staticFilesRoute);
            resourceHandler.setBaseResource(staticResources);
            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{handler, resourceHandler});
            server.setHandler(handlers);
        }

        try {
            System.out.println("== " + NAME + " has ignited ...");
			System.out.println(">> Listening on " + host + ":" + port);

            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    @Override
    public void stop() {
        System.out.print(">>> " + NAME + " shutting down...");
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
        System.out.println("done");
    }

}
