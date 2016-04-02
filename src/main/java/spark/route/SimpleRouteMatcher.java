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
     * @deprecated
     */
    public void parseValidateAddRoute(String route, String acceptType, Object target) {
        add(route, acceptType, target);
    }

    /**
     * @deprecated
     */
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        return find(httpMethod, path, acceptType);
    }

    /**
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
     * @deprecated
     */
    public boolean removeRoute(String path, String httpMethod) {
        return remove(path, httpMethod);
    }

    /**
     * @deprecated
     */
    public boolean removeRoute(String path) {
        return remove(path);
    }

}