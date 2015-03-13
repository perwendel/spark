package spark.route;

/**
 * @author amarseillan
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.utils.MimeParse;

public abstract class RouteMatcher {
	
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RouteMatcher.class);
    private static final char SINGLE_QUOTE = '\'';

	   /**
     * Parse and validates a route and adds it
     *
     * @param route      the route path
     * @param acceptType the accept type
     * @param target     the invocation target
     */
    public void parseValidateAddRoute(String route, String acceptType, Object target) {
    	try {
            int singleQuoteIndex = route.indexOf(SINGLE_QUOTE);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase(); // NOSONAR
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim(); // NOSONAR

            // Use special enum stuff to get from value
            HttpMethod method;
            try {
                method = HttpMethod.valueOf(httpMethod);
            } catch (IllegalArgumentException e) {
                LOG.error("The @Route value: "
                                  + route
                                  + " has an invalid HTTP method part: "
                                  + httpMethod
                                  + ".");
                return;
            }
            addRoute(method, url, acceptType, target);
        } catch (Exception e) {
            LOG.error("The @Route value: " + route + " is not in the correct format", e);
        }
    }
    
    abstract void addRoute(HttpMethod method, String route, String acceptType, Object target);
    
    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path       the path
     * @param acceptType the accept type
     * @return the target
     */
    public abstract RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);
    
    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path       the route path
     * @param acceptType the accept type
     * @return the targets
     */
    public abstract List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);
    
    /**
     * ¨Clear all routes
     */
    public abstract void clearRoutes();
    
    // TODO: I believe this feature has impacted performance. Optimization?
    RouteEntry findTargetWithGivenAcceptType(List<RouteEntry> routeMatches, String acceptType) {
        if (acceptType != null && routeMatches.size() > 0) {
            Map<String, RouteEntry> acceptedMimeTypes = getAcceptedMimeTypes(routeMatches);
            String bestMatch = MimeParse.bestMatch(acceptedMimeTypes.keySet(), acceptType);

            if (routeWithGivenAcceptType(bestMatch)) {
                return acceptedMimeTypes.get(bestMatch);
            } else {
                return null;
            }
        } else {
            if (routeMatches.size() > 0) {
                return routeMatches.get(0);
            }
        }

        return null;
    }
    
    boolean routeWithGivenAcceptType(String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals(bestMatch);
    }
    
    //can be cached? I don't think so.
    private Map<String, RouteEntry> getAcceptedMimeTypes(List<RouteEntry> routes) {
        Map<String, RouteEntry> acceptedTypes = new HashMap<>();

        for (RouteEntry routeEntry : routes) {
            if (!acceptedTypes.containsKey(routeEntry.acceptedType)) {
                acceptedTypes.put(routeEntry.acceptedType, routeEntry);
            }
        }

        return acceptedTypes;
    }
    
    public enum MatcherImplementation {
    	list, trie
    }
    
}
