package spark.webserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.HaltException;
import spark.Request;
import spark.RequestResponseFactory;
import spark.Response;
import spark.Route;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;

public class TargetInvocation {
    
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse; 
    private String uri;
    private RouteMatcher routeMatcher;

    public TargetInvocation(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String uri,
            RouteMatcher routeMatcher) {
        super();
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.uri = uri;
        this.routeMatcher = routeMatcher;
    }

    protected String invokeTargetMethod(String bodyContent) {
        
        String httpMethodStr = httpRequest.getMethod().toLowerCase();
        HttpMethod httpMethod = HttpMethod.valueOf(httpMethodStr);
        
        RouteMatch match = routeMatcher.findTargetForRequestedRoute(HttpMethod.valueOf(httpMethodStr), uri);
        
        Object target = null;
        if (match != null) {
            target = match.getTarget();
        } else if (httpMethod == HttpMethod.head && bodyContent == null) {
            // See if get is mapped to provide default head mapping
            bodyContent = routeMatcher.findTargetForRequestedRoute(HttpMethod.get, uri) != null ? "" : null;
        }

        if (target != null) {
            try {
                Object result = null;
                if (target instanceof Route) {
                    Route route = ((Route) target);
                    Request request = RequestResponseFactory.create(match, httpRequest);
                    Response response = RequestResponseFactory.create(httpResponse);
                    
                    RequestWrapper req = new RequestWrapper();
                    ResponseWrapper res = new ResponseWrapper();
                    req.setDelegate(request);
                    res.setDelegate(response);
                    
                    result = route.handle(req, res);
                }
                if (result != null) {
                    bodyContent = result.toString();
                }
            } catch (HaltException hEx) {
                throw hEx;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bodyContent;
    }
}
