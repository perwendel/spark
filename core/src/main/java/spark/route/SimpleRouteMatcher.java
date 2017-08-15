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

import java.util.List;

import spark.routematch.RouteMatch;

/**
 * Kept just for not breaking API.
 *
 * @deprecated see {@link spark.route.Routes}
 */
public class SimpleRouteMatcher extends Routes {

    /**
     * @param route      the route
     * @param acceptType the accept type
     * @param target     the target
     * @deprecated
     */
    public void parseValidateAddRoute(String route, String acceptType, Object target) {
        add(route, acceptType, target);
    }

    /**
     * @param httpMethod the HttpMethod
     * @param path       the path
     * @param acceptType the accept type
     * @return the RouteMatch object
     * @deprecated
     */
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        return find(httpMethod, path, acceptType);
    }

    /**
     * @param httpMethod the HttpMethod
     * @param path       the path
     * @param acceptType the accept type
     * @return list of RouteMatch objects
     * @deprecated
     */
    public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        return findMultiple(httpMethod, path, acceptType);
    }

    /**
     * @deprecated
     */
    public void clearRoutes() {
        clear();
    }

    /**
     * @param path       the path
     * @param httpMethod the http method name
     * @return true if route removed, false otherwise
     * @deprecated
     */
    public boolean removeRoute(String path, String httpMethod) {
        return remove(path, httpMethod);
    }

    /**
     * @param path   the path
     * @return true if route removed, false otherwise
     * @deprecated
     */
    public boolean removeRoute(String path) {
        return remove(path);
    }

}
