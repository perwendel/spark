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
import java.util.List;

import spark.utils.MimeParse;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class SimpleRouteMatcher extends RouteMatcher{

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleRouteMatcher.class);

    private List<RouteEntry> routes;

    /**
     * Constructor
     */
    public SimpleRouteMatcher() {
        routes = new ArrayList<RouteEntry>();
    }

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path       the path
     * @param acceptType the accept type
     * @return the target
     */
    @Override
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
    @Override
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
    @Override
    public void clearRoutes() {
        routes.clear();
    }

    //////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////

    @Override
    void addRoute(HttpMethod method, String url, String acceptedType, Object target) {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = method;
        entry.path = url;
        entry.target = target;
        entry.acceptedType = acceptedType;
        LOG.debug("Adds route: " + entry);
        // Adds to end of list
        routes.add(entry);
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

}
