/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
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
 * TODO: discover new TODOs.
 * 
 * TODO: Before method for filters...check sinatra page
 * 
 * TODO: Make available as maven dependency, upload on repo etc...
 * TODO: Add *, splat possibility
 * TODO: Add validation of routes, invalid characters and stuff, also validate parameters, check static, ONGOING
 * 
 * TODO: Javadoc
 * 
 * TODO: Create maven archetype, "ONGOING"
 * TODO: Add cache-control helpers
 * 
 * advanced TODO list:
 * TODO: sessions? (use session servlet context?)
 * TODO: Add regexp URIs 
 * 
 * Ongoing
 * 
 * Done
 * TODO: Setting Headers
 * TODO: Do we want get-prefixes for all *getters* or do we want a more ruby like approach??? (Maybe have two choices?)
 * TODO: Setting Body, Status Code
 * TODO: Add possibility to set content type on return, DONE
 * TODO: Add possibility to access HttpServletContext in method impl, DONE
 * TODO: Redirect func in web context, DONE
 * TODO: Refactor, extract interfaces, DONE
 * TODO: Figure out a nice name, DONE - SPARK
 * TODO: Add /uri/{param} possibility, DONE
 * TODO: Tweak log4j config, DONE
 * TODO: Query string in web context, DONE
 * TODO: Add URI-param fetching from webcontext ie. ?param=value&param2=...etc, AND headers, DONE
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
                    route.set(match, httpRequest, httpResponse);
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
