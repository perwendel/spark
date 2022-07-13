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

import spark.utils.SparkUtils;

/**
 * Class that holds information about routes
 *
 * @author Per Wendel
 */
class RouteEntry {

    HttpMethod httpMethod;
    String path;
    String acceptedType;
    Object target;

    RouteEntry() {
    }

    RouteEntry(RouteEntry entry) {
        this.httpMethod = entry.httpMethod;
        this.path = entry.path;
        this.acceptedType = entry.acceptedType;
        this.target = entry.target;
    }

    boolean matches(HttpMethod httpMethod, String path) {
        if ((httpMethod == HttpMethod.before || httpMethod == HttpMethod.after || httpMethod == HttpMethod.afterafter)
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

    private boolean matchPath(String path) { // NOSONAR
        if (!this.path.endsWith("*") && ((path.endsWith("/") && !this.path.endsWith("/")) // NOSONAR
                || (this.path.endsWith("/") && !path.endsWith("/")))) {
            // One and not both ends with slash
            return false;
        }

        if (this.path.equals(path)) {
            // Paths are the same
            return true;
        }

        List<String> thisPathList = SparkUtils.convertRouteToList(this.path);
        List<String> pathList = SparkUtils.convertRouteToList(path);

        return matchPath(thisPathList, pathList, 0, 0);
    }

    private boolean matchPath(List<String> thisPathList, List<String> pathList, int i, int j) {
        if (i == thisPathList.size() && j == pathList.size()) {
            return true;
        }

        if (i == thisPathList.size() || j == pathList.size()) {
            return false;
        }

        String thisPathPart = thisPathList.get(i);
        String pathPart = pathList.get(j);

        if (SparkUtils.isSplat(thisPathPart)) {
            if (i == thisPathList.size() - 1) {
                return true;
            }

            for (int k = j+1 ; k < pathList.size() ; k++) {
                if (matchPath(thisPathList, pathList, i+1, k)) {
                    return true;
                }
            }
        } else if (SparkUtils.isParam(thisPathPart) || thisPathPart.equals(pathPart)) {
            return matchPath(thisPathList, pathList, i+1, j+1);
        }

        return false;
    }

    @Override
    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}
