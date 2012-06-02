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

import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import spark.route.RouteMatcherFactory;

/**
 * 
 * @author Per Wendel
 */
public class SparkServerFactory {

    public static SparkServer create(String staticResourceBase,
            String staticVirtualDirectory, boolean allowDirectoryListings) {
        MatcherFilter matcherFilter = new MatcherFilter(
                RouteMatcherFactory.get(), false);
        matcherFilter.init(null);
        JettyHandler sparkHandler = new JettyHandler(matcherFilter);
        if (staticVirtualDirectory != null && exists(staticResourceBase)) {
            // Configure a handler for serving static files and merge it with
            // the sparkHandler
            HandlerList handlers = new HandlerList();
            ServletContextHandler staticContextHandler = new ServletContextHandler(
                    0);
            staticContextHandler.setContextPath(staticVirtualDirectory);            
            staticContextHandler.addServlet(DefaultServlet.class, "/");            
            // Jetty was sending text files as ISO-8859-1 even when the file
            // was encoded as utf-8, this is done to fix that.
            staticContextHandler.addFilter(CharsetFilter.class, "/*", 1);
            ServletHolder staticServlet = new ServletHolder(
                    new DefaultServlet());
            staticContextHandler.setErrorHandler(new StaticErrorHandler());
            staticServlet.setInitParameter("dirAllowed",
                    Boolean.valueOf(allowDirectoryListings).toString());
            staticServlet.setInitParameter("resourceBase",
                    getAbsoluteUrl(staticResourceBase));
            staticContextHandler.addServlet(staticServlet, "/");
            handlers.addHandler(staticContextHandler);
            handlers.addHandler(sparkHandler);
            return new SparkServerImpl(handlers);
        }
        return new SparkServerImpl(sparkHandler);
    }

    private static boolean exists(String staticResourceBase) {
        try {
            return Resource.newResource(getAbsoluteUrl(staticResourceBase))
                    .exists();
        } catch (Exception e) {
            return false;
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Thread.currentThread().getContextClassLoader()
                .getResource(relativeUrl).toString();
    }
}
