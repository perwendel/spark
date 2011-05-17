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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.Route;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;

/**
 *
 *
 * @author Per Wendel
 */
class MatcherFilter implements Filter {

    private RouteMatcher routeMatcher;
    
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(MatcherFilter.class);
    
    public MatcherFilter(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }
    
    public void init(FilterConfig filterConfig) {
        
    }
    
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
                    throws IOException, ServletException {
        long t0 = System.currentTimeMillis();
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        
        String httpMethod = httpRequest.getMethod().toLowerCase();
        String uri = httpRequest.getRequestURI().toLowerCase();
        
        LOG.debug("httpMethod:" + httpMethod + ", uri: " + uri);
        
        RouteMatch match = routeMatcher.findTargetForRequestedRoute(HttpMethod.valueOf(httpMethod), uri);
        
        Object target = null;
        if (match != null) {
            target = match.getTarget();
        }
        
        if (target != null) {
            try {
                Object result = null;
                if (target instanceof Route) {
                    Route route = ((Route) target);
                    Request request = RequestResponseFactory.create(match, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);
                    result = route.handle(request, response);
                }
                if (result != null) {
                    httpResponse.getOutputStream().write(result.toString().getBytes("utf-8"));
                }
                long t1 = System.currentTimeMillis() - t0;
                LOG.debug("Time for request: " + t1);
            } catch (Exception e) {
                LOG.error(e);
                httpResponse.sendError(500);
            }    
        } else {
            httpResponse.setStatus(404);
            httpResponse.getOutputStream().write(NOT_FOUND.getBytes("utf-8"));
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }
    
    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>The requested route has not been mapped in Spark</body></html>";
}
