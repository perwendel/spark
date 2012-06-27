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

import com.yammer.metrics.reporting.MetricsServlet;
import com.yammer.metrics.reporting.PingServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Spark server implementation
 *
 * @author Per Wendel
 */
class SparkServerImpl implements SparkServer
{
    private static final String NAME = "Spark";

    private Handler handler;

    private Object lock = new Object();

    private boolean stopped = false;

    public SparkServerImpl(final Handler handler)
    {
        this.handler = handler;
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");
    }

    @Override
    public void ignite()
    {
        ignite(4567);
    }

    @Override
    public void ignite(int port)
    {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        final ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
        servletContextHandler.addServlet(PingServlet.class, "/ping");
        final ServletHolder metricsServletHolder = servletContextHandler.addServlet(MetricsServlet.class, "/metrics");
        metricsServletHolder.setInitParameter("show-jvm-metrics", "false");

        final HandlerList handlerList = new HandlerList();
        handlerList.addHandler(servletContextHandler);
        handlerList.addHandler(handler);

        server.setHandler(handlerList);

        try
        {
            System.out.println("== " + NAME + " has ignited ...");
            System.out.println(">> Listening on 0.0.0.0:" + port);

            server.start();

            synchronized (lock)
            {
                while (!stopped)
                {
                    try
                    {
                        lock.wait();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }

            System.out.println(">>> " + NAME + " shutting down...");

            server.stop();
            server.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(100);
        }
    }

    @Override
    public void stop()
    {
        synchronized (lock)
        {
            stopped = true;
            lock.notifyAll();
        }
    }
}
