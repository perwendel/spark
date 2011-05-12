package spark.route;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.log4j.Logger;

import spark.Route;
import spark.annotation.AnnotationFinder;
import spark.annotation.AnnotationFinderFactory;

public class RouteMatcherFactory {
    
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(RouteMatcherFactory.class);
    
    public static RouteMatcher create() {
        RouteMatcherImpl matcher = new RouteMatcherImpl();
        AnnotationFinder annotationFinder = AnnotationFinderFactory.get();
        
        Set<Method> annotated = annotationFinder.findMethodsAnnotatedWith(Route.class);
        LOG.info("Size: " + annotated.size());
        for (Method method : annotated) {
            Route s = method.getAnnotation(Route.class);
            String route = s.value().toLowerCase().trim();
            LOG.info("s: method: " + method + ", route: " + route);
            
            // Parse route string to get HttpMethod and route
            matcher.parseValidateAddRoute(route, method);
            
            // TODO: Remember in a future paramContext ---> TO LOWER CASE !!!
        }
        return matcher;
    }
    
}
