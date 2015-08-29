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
package spark.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Access;
import spark.Spark;
import spark.SparkBase;
import spark.resource.*;
import spark.route.RouteMatcherFactory;
import spark.session.CookieSessionHandler;
import spark.utils.IOUtils;
import spark.webserver.MatcherFilter;
import spark.webserver.SparkHttpResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter that can be configured to be used in a web.xml file.
 * Needs the init parameter 'applicationClass' set to the application class where
 * the adding of routes should be made.
 *
 * @author Per Wendel
 */
public class SparkFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(SparkFilter.class);

    public static final String APPLICATION_CLASS_PARAM = "applicationClass";

    private static List<AbstractResourceHandler> staticResourceHandlers = null;

    private static boolean staticResourcesSet = false;
    private static boolean externalStaticResourcesSet = false;

    private String filterPath;
    private MatcherFilter matcherFilter;
    private SparkApplication application;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Access.runFromServlet();

        application = getApplication(filterConfig);
        application.init();

        filterPath = FilterTools.getFilterPath(filterConfig);
        matcherFilter = new MatcherFilter(RouteMatcherFactory.get(), true, false);
    }

    /**
     * Returns an instance of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class name in the filterConfig using the key {@link #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param filterConfig the filter configuration for retrieving parameters passed to this filter.
     * @return the spark application containing the configuration.
     * @throws ServletException if anything went wrong.
     */
    protected SparkApplication getApplication(FilterConfig filterConfig) throws ServletException {
        try {
            String applicationClassName = filterConfig.getInitParameter(APPLICATION_CLASS_PARAM);
            Class<?> applicationClass = Class.forName(applicationClassName);
            return (SparkApplication) applicationClass.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
                                                                                              IOException,
                                                                                              ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; // NOSONAR

        final String relativePath = FilterTools.getRelativePath(httpRequest, filterPath);

        LOG.debug(relativePath);

        SparkFilterRequestWrapper requestWrapper =
                new SparkFilterRequestWrapper((HttpServletRequest) request, SparkBase.getSessionStrategy(), relativePath);

        SparkHttpResponseWrapper responseWrapper = new SparkHttpResponseWrapper(requestWrapper, (HttpServletResponse) response);

        // handle static resources
        if (staticResourceHandlers != null) {
            for (AbstractResourceHandler staticResourceHandler : staticResourceHandlers) {
                AbstractFileResolvingResource resource = staticResourceHandler.getResource(httpRequest);
                if (resource != null && resource.isReadable()) {
                    IOUtils.copy(resource.getInputStream(), responseWrapper.getOutputStream());
                    return;
                }
            }
        }

        matcherFilter.doFilter(requestWrapper, responseWrapper, chain);

        responseWrapper.flushBuffer();
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureStaticResources(String folder) {
        if (!staticResourcesSet) {
            if (folder != null) {
                try {
                    ClassPathResource resource = new ClassPathResource(folder);
                    if (resource.getFile().isDirectory()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<>();
                        }
                        staticResourceHandlers.add(new ClassPathResourceHandler(folder, "index.html"));
                        LOG.info("StaticResourceHandler configured with folder = " + folder);
                    } else {
                        LOG.error("Static resource location must be a folder");
                    }
                } catch (IOException e) {
                    LOG.error("Error when creating StaticResourceHandler", e);
                }
            }
            staticResourcesSet = true;
        }
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureExternalStaticResources(String folder) {
        if (!externalStaticResourcesSet) {
            if (folder != null) {
                try {
                    ExternalResource resource = new ExternalResource(folder);
                    if (resource.getFile().isDirectory()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<>();
                        }
                        staticResourceHandlers.add(new ExternalResourceHandler(folder, "index.html"));
                        LOG.info("External StaticResourceHandler configured with folder = " + folder);
                    } else {
                        LOG.error("External Static resource location must be a folder");
                    }
                } catch (IOException e) {
                    LOG.error("Error when creating external StaticResourceHandler", e);
                }
            }
            externalStaticResourcesSet = true;
        }
    }

    @Override
    public void destroy() {
        if (application != null) {
            application.destroy();
        }
    }

}
