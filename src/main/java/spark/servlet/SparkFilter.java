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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.SparkInstance;
import spark.webserver.MatcherFilter;

/**
 * Filter that can be configured to be used in a web.xml file.
 * Needs the init parameter 'applicationClass' set to the application class where
 * the adding of routes should be made.
 *
 * @author Per Wendel
 */
public class SparkFilter implements Filter {

    public static final String APPLICATION_CLASS_PARAM = "applicationClass";

    private static final Logger LOG = LoggerFactory.getLogger(SparkFilter.class);
    
    private String filterPath;

    private MatcherFilter matcherFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SparkInstance instance = new SparkInstance();

        final SparkApplication application = getApplication(filterConfig);
        application.init(instance);

        filterPath = FilterTools.getFilterPath(filterConfig);
        matcherFilter = new MatcherFilter(instance.getRouteMatcher(), true, false);
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; // NOSONAR
        
        final String relativePath = FilterTools.getRelativePath(httpRequest, filterPath);
        
        LOG.debug(relativePath);
        
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getRequestURI() {
                return relativePath;
            }
        };
        matcherFilter.doFilter(requestWrapper, response, chain);
    }

    @Override
    public void destroy() {
        // ignore
    }

}
