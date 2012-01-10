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
import java.util.List;

import spark.utils.SparkUtils;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class SimpleRouteMatcher implements RouteMatcher {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleRouteMatcher.class);
    
    private List<RouteEntry> routes;

    private static class RouteEntry {

        private HttpMethod httpMethod;
        private String path;
        private Object target;

        private boolean matches(HttpMethod httpMethod, String path) {
            if ( (httpMethod == HttpMethod.before || httpMethod == HttpMethod.after)
                            && (this.httpMethod == httpMethod)
                            && this.path.equals(SparkUtils.ALL_PATHS)) {
                // Is filter and matches all
                return true;
            }
            boolean match = false;
            if (this.httpMethod == httpMethod) {
                match = matchPath(path);
            }
            return match;
        }

        private boolean matchPath(String path) {
            if (!this.path.endsWith("*") && ((path.endsWith("/") && !this.path.endsWith("/")) 
                            || (this.path.endsWith("/") && !path.endsWith("/")))) {
                // One and not both ends with slash
                return false;
            }
            if (this.path.equals(path)) {
                // Paths are the same
                return true;
            }

            // check params
            List<String> thisPathList = SparkUtils.convertRouteToList(this.path);
            List<String> pathList = SparkUtils.convertRouteToList(path);

            
            int thisPathSize = thisPathList.size();
            int pathSize = pathList.size();
            
            if (thisPathSize == pathSize) {
                for (int i = 0; i < thisPathSize; i++) {
                    String thisPathPart = thisPathList.get(i);
                    String pathPart = pathList.get(i);
                    
                    if ((i == thisPathSize -1)) {
                        if (thisPathPart.equals("*") && this.path.endsWith("*")) {
                            // wildcard match
                            return true;
                        }    
                    }
                    
                    if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                        return false;
                    }
                }
                // All parts matched
                return true;
            } else {
                // Number of "path parts" not the same
                // check wild card:
                if (this.path.endsWith("*")) {
                    if (pathSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                        // Hack for making wildcards work with trailing slash
                        pathList.add("");
                        pathList.add("");
                        pathSize += 2;
                    }

                    if (thisPathSize < pathSize) {
                        for (int i = 0; i < thisPathSize; i++) {
                            String thisPathPart = thisPathList.get(i);
                            String pathPart = pathList.get(i);
                            if (thisPathPart.equals("*") && (i == thisPathSize -1) && this.path.endsWith("*")) {
                                // wildcard match
                                return true;
                            }
                            if (!thisPathPart.startsWith(":") && !thisPathPart.equals(pathPart)) {
                                return false;
                            }
                        }
                        // All parts matched
                        return true;
                    }
                    // End check wild card
                }
                return false;
            }
        }

        public String toString() {
            return httpMethod.name() + ", " + path + ", " + target;
        }
    }
    
    public SimpleRouteMatcher() {
        routes = new ArrayList<RouteEntry>();
    }

    @Override
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path) {
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                return new RouteMatch(httpMethod, entry.target, entry.path, path);
            }
        }
        return null;
    }
    
    @Override
    public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path) {
        List<RouteMatch> matchSet = new ArrayList<RouteMatch>();
        for (RouteEntry entry : routes) {
            if (entry.matches(httpMethod, path)) {
                matchSet.add(new RouteMatch(httpMethod, entry.target, entry.path, path));
            }
        }
        return matchSet;
    }

    @Override
    public void parseValidateAddRoute(String route, Object target) {
        try {
            int singleQuoteIndex = route.indexOf(SINGLE_QUOTE);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase();
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim().toLowerCase();

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
            addRoute(method, url, target);
        } catch (Exception e) {
            LOG.error("The @Route value: " + route + " is not in the correct format", e);
        }

    }

    private void addRoute(HttpMethod method, String url, Object target) {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = method;
        entry.path = url;
        entry.target = target;
        LOG.debug("Adds route: " + entry);
        // Adds to end of list
        routes.add(entry);
    }

    @Override
    public void clearRoutes() {
        routes.clear();
    }

}
