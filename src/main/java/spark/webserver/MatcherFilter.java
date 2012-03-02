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
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;

import spark.Access;
import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;

/**
 * Filter for matching of filters and routes.
 * 
 * @author Per Wendel
 */
public class MatcherFilter implements Filter {

    private RouteMatcher routeMatcher;
    private boolean isServletContext;

    /** The logger. */
    private org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(getClass());

    /**
     * Constructor
     * 
     * @param routeMatcher The route matcher
     * @param isServletContext If true, chain.doFilter will be invoked if request is not consumed by Spark.
     */
    public MatcherFilter(RouteMatcher routeMatcher, boolean isServletContext) {
        this.routeMatcher = routeMatcher;
        this.isServletContext = isServletContext;
    }

    public void init(FilterConfig filterConfig) {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                    FilterChain chain) throws IOException, ServletException {
        long t0 = System.currentTimeMillis();
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String httpMethodStr = httpRequest.getMethod().toLowerCase();
        String uri = httpRequest.getRequestURI().toLowerCase();

        String bodyContent = null;

        LOG.debug("httpMethod:" + httpMethodStr + ", uri: " + uri);
        try {
            bodyContent = invokeBeforeFilters(httpRequest, httpResponse, uri);
            bodyContent = invokeTargetMethod(httpRequest, httpResponse, uri, bodyContent);

            long t1 = System.currentTimeMillis() - t0;
            LOG.debug("Time for request: " + t1 + "ms");
            bodyContent = invokeAfterFilters(httpRequest, httpResponse, uri, bodyContent);
        } catch (HaltException hEx) {
            LOG.debug("halt performed");
            httpResponse.setStatus(hEx.getStatusCode());
            if (hEx.getBody() != null) {
                bodyContent = hEx.getBody();
            } else {
                bodyContent = "";
            }
        }

        boolean consumed = bodyContent != null ? true : false;

        LOG.debug("consumed: {}, bodyContent: {}", consumed, bodyContent);

        if (!consumed && !isServletContext) {
            httpResponse.setStatus(404);
            bodyContent = NOT_FOUND;
            consumed = true;
        }

        if (consumed) {
            // Write body content
            httpResponse.getOutputStream().write(bodyContent.getBytes("utf-8"));
        } else if (chain != null) {
            chain.doFilter(httpRequest, httpResponse);
        } else {
            Log.warn("No bodyContent available");
        }
    }

    protected String invokeTargetMethod(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String uri,
            String bodyContent) {

        List<RouteMatch> interceptors = routeMatcher.findTargetsForRequestedRoute(HttpMethod.around, uri);

        try {
            TargetInvocation targetInvocation = new TargetInvocation(httpRequest, httpResponse, uri, routeMatcher);
            SimpleInterceptorChain chain = new SimpleInterceptorChain(httpRequest, httpResponse, targetInvocation ,
                    interceptors);
            chain.invokeNext();
            bodyContent = chain.getBodyContent();

        } catch (HaltException hEx) {
            throw hEx;
        } catch (Exception e) {
            LOG.error("", e);
            httpResponse.setStatus(500);
            bodyContent = INTERNAL_ERROR;
        }
        return bodyContent;
    }
    
    protected String invokeBeforeFilters(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String uri) {
        return invokeFilters(HttpMethod.before, httpRequest, httpResponse, uri, null);
    }
    
    protected String invokeAfterFilters(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String uri,
            String bodyContent) {
        return invokeFilters(HttpMethod.after, httpRequest, httpResponse, uri, bodyContent);
    }
    
    protected String invokeFilters(
            HttpMethod when,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, //
            String uri, String bodyContent) {
        List<RouteMatch> matchSet = routeMatcher.findTargetsForRequestedRoute(when, uri);
        RequestWrapper req = new RequestWrapper();
        ResponseWrapper res = new ResponseWrapper();
        
        for (RouteMatch filterMatch : matchSet) {
            Object filterTarget = filterMatch.getTarget();
            if (filterTarget != null && filterTarget instanceof spark.Filter) {
                Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                Response response = RequestResponseFactory.create(httpResponse);
                
                req.setDelegate(request);
                res.setDelegate(response);
                
                spark.Filter filter = (spark.Filter) filterTarget;
                filter.handle(req, res);

                String bodyAfterFilter = Access.getBody(response);
                if (bodyAfterFilter != null) {
                    bodyContent = bodyAfterFilter;
                }
            }   
        }
        return bodyContent;
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2>The requested route has not been mapped in Spark</body></html>";
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
}
