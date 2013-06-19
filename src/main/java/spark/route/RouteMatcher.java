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



/**
 * Route matcher
 *
 * @author Per Wendel
 */
public interface RouteMatcher {
    
    String ROOT = "/";
    char SINGLE_QUOTE = '\'';
    
    /**
     * Parses, validates and adds a route
     * 
     * @param route
     * @param acceptType
     * @param target
     */
    void parseValidateAddRoute(String route, String acceptType, Object target);

    /**
     * Finds the a target route for the requested route path and accept type
     * @param httpMethod
     * @param path
     * @param acceptType
     * @return
     */
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);
    
    /**
     * Clear all routes
     */
    void clearRoutes();

	List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType);

}
