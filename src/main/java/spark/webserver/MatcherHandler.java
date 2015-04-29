package spark.webserver;

import spark.*;
import spark.exception.ExceptionHandlerImpl;
import spark.exception.ExceptionMapper;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.SimpleRouteMatcher;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handler for matching of filters and routes.
 *
 * @author Per Wendel
 */
public class MatcherHandler {

    private static final String ACCEPT_TYPE_REQUEST_MIME_HEADER = "Accept";
    private static final String HTTP_METHOD_OVERRIDE_HEADER = "X-HTTP-Method-Override";

    private SimpleRouteMatcher routeMatcher;
    private boolean hasOtherHandlers;

    /**
     * The logger.
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MatcherHandler.class);

    /**
     * Constructor
     *
     * @param routeMatcher     The route matcher
     * @param hasOtherHandlers If true, do nothing if request is not consumed by Spark in order to let others handlers process the request.
     */
    public MatcherHandler(SimpleRouteMatcher routeMatcher, boolean hasOtherHandlers) {
        this.routeMatcher = routeMatcher;
        this.hasOtherHandlers = hasOtherHandlers;
    }

    public void init(InitParameters initParameter) {
        //
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException { // NOSONAR
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest; // NOSONAR
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String method = httpRequest.getHeader(HTTP_METHOD_OVERRIDE_HEADER);
        if (method == null) {
            method = httpRequest.getMethod();
        }
        String httpMethodStr = method.toLowerCase(); // NOSONAR
        String uri = httpRequest.getRequestURI(); // NOSONAR
        String acceptType = httpRequest.getHeader(ACCEPT_TYPE_REQUEST_MIME_HEADER);

        String bodyContent = null;

        RequestWrapper requestWrapper = new RequestWrapper();
        ResponseWrapper responseWrapper = new ResponseWrapper();

        Response response = RequestResponseFactory.create(httpResponse);

        LOG.debug("httpMethod:" + httpMethodStr + ", uri: " + uri);
        try {
            // BEFORE filters
            List<RouteMatch> matchSet = routeMatcher.findTargetsForRequestedRoute(HttpMethod.before, uri, acceptType);

            for (RouteMatch filterMatch : matchSet) {
                Object filterTarget = filterMatch.getTarget();
                if (filterTarget instanceof FilterImpl) {
                    Request request = RequestResponseFactory.create(filterMatch, httpRequest);

                    FilterImpl filter = (FilterImpl) filterTarget;

                    requestWrapper.setDelegate(request);
                    responseWrapper.setDelegate(response);

                    filter.handle(requestWrapper, responseWrapper);

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

                        if (requestWrapper.getDelegate() == null) {
                            Request request = RequestResponseFactory.create(match, httpRequest);
                            requestWrapper.setDelegate(request);
                        } else {
                            requestWrapper.changeMatch(match);
                        }

                        responseWrapper.setDelegate(response);

                        Object element = route.handle(requestWrapper, responseWrapper);

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

                    if (requestWrapper.getDelegate() == null) {
                        Request request = RequestResponseFactory.create(filterMatch, httpRequest);
                        requestWrapper.setDelegate(request);
                    } else {
                        requestWrapper.changeMatch(filterMatch);
                    }

                    responseWrapper.setDelegate(response);

                    FilterImpl filter = (FilterImpl) filterTarget;
                    filter.handle(requestWrapper, responseWrapper);

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
                handler.handle(e, requestWrapper, responseWrapper);
                String bodyAfterFilter = Access.getBody(responseWrapper.getDelegate());
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
        if (bodyContent == null && responseWrapper.isRedirected()) {
            bodyContent = "";
        }

        boolean consumed = bodyContent != null;

        if (!consumed && hasOtherHandlers) {
            throw new NotConsumedException();
        }

        // OLD behaviour: //if (!consumed && !isServletContext) {
        // once spark started using Servlet instead of Filter, there is no more need
        // to give another filter/servlet chance to change body content. Servlet is
        // last item in execution chain
        if (!consumed) {
            LOG.info("The requested route [" + uri + "] has not been mapped in Spark");
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            bodyContent = String.format(NOT_FOUND);
        }

        // Write body content
        if (!httpResponse.isCommitted()) {
            if (httpResponse.getContentType() == null) {
                httpResponse.setContentType("text/html; charset=utf-8");
            }
            httpResponse.getOutputStream().write(bodyContent.getBytes("utf-8"));
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2></body></html>";
    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";
}
