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

import spark.Access;
import spark.FilterImpl;
import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.RouteImpl;
import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.SimpleRouteMatcher;

/**
 * Filter for matching of filters and routes.
 *
 * @author Per Wendel
 */
public class MatcherFilter implements Filter {

    private static final String ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept";

    private SimpleRouteMatcher routeMatcher;
    private boolean isServletContext;
    private boolean hasOtherHandlers;

    /**
     * The logger.
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MatcherFilter.class);

    /**
     * Constructor
     *
     * @param routeMatcher     The route matcher
     * @param isServletContext If true, chain.doFilter will be invoked if request is not consumed by Spark.
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Spark in order to let others handlers process the request.
     */
    public MatcherFilter(SimpleRouteMatcher routeMatcher, boolean isServletContext, boolean hasOtherHandlers) {
        this.routeMatcher = routeMatcher;
        this.isServletContext = isServletContext;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    public void init(FilterConfig filterConfig) {
        //
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, // NOSONAR
                         FilterChain chain) throws IOException, ServletException { // NOSONAR
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest; // NOSONAR
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String httpMethodStr = httpRequest.getMethod().toLowerCase(); // NOSONAR
        String uri = httpRequest.getRequestURI(); // NOSONAR
        String acceptType = httpRequest.getHeader(ACCEPT_TYPE_REQUEST_MIME_HEADER);

        String bodyContent = null;

        RequestWrapper req = new RequestWrapper();
        ResponseWrapper res = new ResponseWrapper();

        LOG.debug("httpMethod:" + httpMethodStr + ", uri: " + uri);
        try {
            // BEFORE filters
            List<RouteMatch> matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.before, uri, acceptType);

            for (RouteMatch filterMatch : matchSet) {
                Object filterTarget = filterMatch.getTarget();
                if (filterTarget instanceof FilterImpl) {
                    Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);

                    FilterImpl filter = (FilterImpl) filterTarget;

                    req.setDelegate(request);
                    res.setDelegate(response);

                    filter.handle(req, res);

                    String bodyAfterFilter = Access.getBody(response);
                    if (bodyAfterFilter != null) {
                        bodyContent = bodyAfterFilter;
                    }
                }
            }
            // BEFORE filters, END

            HttpMethod httpMethod = HttpMethod.valueOf(httpMethodStr);

            RouteMatch match = null;
            match = routeMatcher.findTargetForRequestedRoute(httpMethod, uri, acceptType);

            Object target = null;
            if (match != null) {
                target = match.getTarget();
            } else if (httpMethod == HttpMethod.head && bodyContent == null) {
                // See if get is mapped to provide default head mapping
                bodyContent =
                        routeMatcher.findTargetForRequestedRoute(HttpMethod.get, uri, acceptType) != null ? "" : null;
            }

            if (target != null) {
                try {
                    String result = null;
                    if (target instanceof RouteImpl) {
                        RouteImpl route = ((RouteImpl) target);
                        Request request = RequestResponseFactory.create(match, httpRequest);
                        Response response = RequestResponseFactory.create(httpResponse);

                        req.setDelegate(request);
                        res.setDelegate(response);

                        Object element = route.handle(req, res);

                        result = route.render(element);
                        // result = element.toString(); // TODO: Remove later when render fixed
                    }
                    if (result != null) {
                        bodyContent = result;
                    }
                } catch (HaltException hEx) { // NOSONAR
                    throw hEx; // NOSONAR
                }
            }

            // AFTER filters
            matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.after, uri, acceptType);

            for (RouteMatch filterMatch : matchSet) {
                Object filterTarget = filterMatch.getTarget();
                if (filterTarget instanceof FilterImpl) {
                    Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);

                    req.setDelegate(request);
                    res.setDelegate(response);

                    FilterImpl filter = (FilterImpl) filterTarget;
                    filter.handle(req, res);

                    String bodyAfterFilter = Access.getBody(response);
                    if (bodyAfterFilter != null) {
                        bodyContent = bodyAfterFilter;
                    }
                }
            }
            // AFTER filters, END

        } catch (HaltException hEx) {
            LOG.debug("halt performed");
            httpResponse.setStatus(hEx.getStatusCode());
            if (hEx.getBody() != null) {
                bodyContent = hEx.getBody();
            } else {
                bodyContent = "";
            }
        } catch (Exception e) {
            ExceptionHandlerImpl handler = ExceptionMapper.getInstance().getHandler(e);
            if (handler != null) {
                handler.handle(e, req, res);
                String bodyAfterFilter = Access.getBody(res.getDelegate());
                if (bodyAfterFilter != null) {
                    bodyContent = bodyAfterFilter;
                }
            } else {
                LOG.error("", e);
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                bodyContent = INTERNAL_ERROR;
            }
        }

        // If redirected and content is null set to empty string to not throw NotConsumedException
        if (bodyContent == null && res.isRedirected()) {
            bodyContent = "";
        }

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers) {
            throw new NotConsumedException();
        }

        if (!consumed && !isServletContext) {
            LOG.info("The requested route [" + uri + "] has not been mapped in Spark");
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            bodyContent = String.format(NOT_FOUND);
            consumed = true;
        }

        if (consumed) {
            // Write body content
            if (!httpResponse.isCommitted()) {
                if (httpResponse.getContentType() == null) {
                    httpResponse.setContentType("text/html; charset=utf-8");
                }
                httpResponse.getOutputStream().write(bodyContent.getBytes("utf-8"));
            }
        } else if (chain != null) {
            chain.doFilter(httpRequest, httpResponse);
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2></body></html>";
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
}
