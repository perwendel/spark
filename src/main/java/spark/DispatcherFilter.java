package spark;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

/**
 * TODO: discover new TODOs.
 * 
 * TODO: Add /uri/{param} possibility
 * TODO: Add possibility to access HttpServletContext in method impl.
 * TODO: Figure out a nice name
 * TODO: Add possibility to set content type on return
 * TODO: Create maven archetype
 * TODO: Tweak log4j config
 * TODO: Add regexp URIs
 *
 * @author Per Wendel
 */
public class DispatcherFilter implements Filter {

    private RouteMatcher routeMatcher;
    
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DispatcherFilter.class);
    
    public void init(FilterConfig filterConfig) {
        routeMatcher = new RouteMatcher();
        
        Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> annotated = reflections.getMethodsAnnotatedWith(Route.class);
        
        LOG.info("Size: " + annotated.size());
        
        for (Method method : annotated) {
            Route s = method.getAnnotation(Route.class);
            String route = s.value().toLowerCase().trim();
            LOG.info("s: method: " + method + ", route: " + route);
            
            // Parse route string to get HttpMethod and route
            parseAndAddRoute(route, method);
            
            // TODO: Remember in a future paramContext ---> TO LOWER CASE !!!
        }
    }
    
    private void parseAndAddRoute(String route, Method target) {
        System.out.println("Route: " + route);
        int singleQuoteIndex = route.indexOf('\'');
        System.out.println(singleQuoteIndex);
        String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase();
        String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim().toLowerCase();
        System.out.println(httpMethod + ", " + url);
    
        // Use special enum stuff to get from value
        HttpMethod method = HttpMethod.valueOf(httpMethod);
        routeMatcher.addRoute(method, url, target);
    }
    
    public static void print() {
        System.out.println("//balle///:param///");
    }
    
    public static void print2() {
        System.out.println("balle/fjonger/dong");
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(System.out.getClass().getName());
        
        RouteMatcher matcher = new RouteMatcher();
        matcher.addRoute(HttpMethod.get, "//balle///:param///", DispatcherFilter.class.getMethod("print"));
        matcher.addRoute(HttpMethod.get, "balle/fjonger/dong", DispatcherFilter.class.getMethod("print2"));
        
        Method target = matcher.findTargetForRoute(HttpMethod.get, "/balle/fjongera/dong").getTarget();
        try {
            target.invoke(target.getDeclaringClass());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
        long t0 = System.currentTimeMillis();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String httpMethod = httpRequest.getMethod().toLowerCase();
        String uri = httpRequest.getRequestURI().toLowerCase();
        
        LOG.info("httpMethod:" + httpMethod + ", uri: " + uri);
        
        RouteMatch match = routeMatcher.findTargetForRoute(HttpMethod.valueOf(httpMethod), uri);

        Method target = null;
        
        if (match != null) {
            new WebContext(match);
            
            // TODO: Do something with the web context
            
            target = match.getTarget();
        }
        
        System.out.println("target: " + target);
        
        if (target != null) {
            try {
                Object result;
                if (Modifier.isStatic(target.getModifiers())) {
                    result = target.invoke(target.getDeclaringClass());
                } else {
                    LOG.warn("Method: '" + target + "' should be static. " + "Spark" + " is nice and will try to invoke it anyways...");
                    result = target.invoke(target.getDeclaringClass().newInstance());
                    // TODO: Should we just throw an explicit exception
                }
                httpResponse.getOutputStream().write(result.toString().getBytes("utf-8"));
                long t1 = System.currentTimeMillis() - t0;
                LOG.info("Time for request: " + t1);
            } catch (Exception e) {
                LOG.error(e);
            }    
        } else {
            httpResponse.sendError(404);    
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }
}
