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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.globalstate.ServletFlag;
import spark.http.matching.MatcherFilter;
import spark.route.ServletRoutes;
import spark.staticfiles.StaticFilesConfiguration;
import spark.utils.StringUtils;

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

    private String filterPath;

    private MatcherFilter matcherFilter;

    /**
     * It contains all the Spark application instances that was declared in the filter configuration. They can be one or more
     * class names separated by commas.
     */
    private SparkApplication[] applications;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletFlag.runFromServlet();

        applications = getApplications(filterConfig);

        for (SparkApplication application : applications) {
            application.init();
        }

        filterPath = FilterTools.getFilterPath(filterConfig);

        matcherFilter = new MatcherFilter(ServletRoutes.get(), StaticFilesConfiguration.servletInstance, true, false);
    }

    /**
     * Returns an instance of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class name in the filterConfig using the key {@value #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param filterConfig the filter configuration for retrieving parameters passed to this filter.
     * @return the spark application containing the configuration.
     * @throws ServletException if anything went wrong.
     * @deprecated Use {@link #getApplications(FilterConfig)} instead.
     */
    @Deprecated
    protected SparkApplication getApplication(FilterConfig filterConfig) throws ServletException {
        return getApplication(filterConfig.getInitParameter(APPLICATION_CLASS_PARAM));
    }

    /**
     * Returns an instance of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class name in the filterConfig using the key {@value #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param applicationClassName the spark application class name passed to this filter.
     * @return the spark application containing the configuration.
     * @throws ServletException if anything went wrong.
     */
    protected SparkApplication getApplication(String applicationClassName) throws ServletException {
        try {
            Class<?> applicationClass = Class.forName(applicationClassName);
            return (SparkApplication) applicationClass.newInstance();
        } catch (Exception exc) {
            throw new ServletException(exc);
        }
    }

    /**
     * Returns the instances of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class names in the filterConfig using the key {@value #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param filterConfig the filter configuration for retrieving parameters passed to this filter.
     * @return the spark applications containing the configuration.
     * @throws ServletException if anything went wrong.
     */
    protected SparkApplication[] getApplications(final FilterConfig filterConfig) throws ServletException {

        String applications = filterConfig.getInitParameter(APPLICATION_CLASS_PARAM);
        SparkApplication[] solvedApplications = null;

        if (StringUtils.isNotBlank(applications)) {
            final String[] sparkApplications = applications.split(",");

            if (sparkApplications != null && sparkApplications.length > 0) {
                solvedApplications = new SparkApplication[sparkApplications.length];

                for (int index = 0; index < sparkApplications.length; index++) {
                    solvedApplications[index] = getApplication(sparkApplications[index].trim());
                }
            } else {
                throw new ServletException("There are no Spark applications configured in the filter.");
            }
        }

        return solvedApplications;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
                                                                                              IOException,
                                                                                              ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; // NOSONAR
        HttpServletResponse httpResponse = (HttpServletResponse) response; // NOSONAR

        final String relativePath = FilterTools.getRelativePath(httpRequest, filterPath);

        if (LOG.isDebugEnabled()) {
            LOG.debug(relativePath);
        }

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getPathInfo() {
                return relativePath;
            }

            @Override
            public String getRequestURI() {
                return relativePath;
            }
        };

        // handle static resources
        boolean consumed = StaticFilesConfiguration.servletInstance.consume(httpRequest, httpResponse);

        if (consumed) {
            return;
        }

        matcherFilter.doFilter(requestWrapper, response, chain);
    }

    @Override
    public void destroy() {
        if (applications != null) {
            for (SparkApplication sparkApplication : applications) {
                sparkApplication.destroy();
            }
        }
    }

}
