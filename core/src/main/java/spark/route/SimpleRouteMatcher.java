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
package spark.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.utils.MimeParse;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class SimpleRouteMatcher {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleRouteMatcher.class);
    private static final char SINGLE_QUOTE = '\'';

    private List<RouteEntry> routes;

    /**
     * Constructor
     */
    public SimpleRouteMatcher() {
        routes = new ArrayList<RouteEntry>();
    }

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

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path       the path
     * @param acceptType the accept type
     * @return the target
     */
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        List<RouteEntry> routeEntries = this.findTargetsForRequestedRoute(httpMethod, path);
        RouteEntry entry = findTargetWithGivenAcceptType(routeEntries, acceptType);
        return entry != null ? new RouteMatch(httpMethod, entry.target, entry.path, path, acceptType) : null;
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path       the route path
     * @param acceptType the accept type
     * @return the targets
     */
    public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        List<RouteMatch> matchSet = new ArrayList<>();
        List<RouteEntry> routeEntries = findTargetsForRequestedRoute(httpMethod, path);

        for (RouteEntry routeEntry : routeEntries) {
            if (acceptType != null) {
                String bestMatch = MimeParse.bestMatch(Arrays.asList(routeEntry.acceptedType), acceptType);

                if (routeWithGivenAcceptType(bestMatch)) {
                    matchSet.add(new RouteMatch(httpMethod, routeEntry.target, routeEntry.path, path, acceptType));
                }
            } else {
                matchSet.add(new RouteMatch(httpMethod, routeEntry.target, routeEntry.path, path, acceptType));
            }
        }

        return matchSet;
    }

    /**
     * ¨Clear all routes
     */
    public void clearRoutes() {
        routes.clear();
    }

    //////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////

    private void addRoute(HttpMethod method, String url, String acceptedType, Object target) {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = method;
        entry.path = url;
        entry.target = target;
        entry.acceptedType = acceptedType;
        LOG.debug("Adds route: " + entry);
        // Adds to end of list
        routes.add(entry);
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

    private boolean routeWithGivenAcceptType(String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals(bestMatch);
    }

    private List<RouteEntry> findTargetsForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteEntry> matchSet = new ArrayList<RouteEntry>();
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(entry);
            }
        }
        return matchSet;
    }

    // TODO: I believe this feature has impacted performance. Optimization?
    private RouteEntry findTargetWithGivenAcceptType(List<RouteEntry> routeMatches, String acceptType) {
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

}
