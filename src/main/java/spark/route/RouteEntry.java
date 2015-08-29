package spark.route;

import java.util.List;

import spark.utils.SparkUtils;

/**
 * Created by Per Wendel on 2014-05-10.
 */
class RouteEntry {

    HttpMethod httpMethod;
    String path;
    String acceptedType;
    Object target;

    boolean matches(HttpMethod httpMethod, String path) {
        if ((httpMethod == HttpMethod.before || httpMethod == HttpMethod.after)
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
        char lastChar = this.path.charAt(this.path.length() - 1);
        if (lastChar != '*') {
            char paramLastChar = path.charAt(path.length() - 1);
            if ((paramLastChar == '/' && lastChar != '/') ||
                    (paramLastChar != '/' && lastChar == '/')) {
                // One and not both ends with slash
                return false;
            }
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
            return matchPathSameSize(thisPathList, pathList, thisPathSize);
        } else {
            return mathPathDifferentSize(path, thisPathList, pathList, thisPathSize, pathSize);

        }
    }

    private boolean mathPathDifferentSize(String path, List<String> thisPathList, List<String> pathList, int thisPathSize, int pathSize) {
        // Number of "path parts" not the same
        // check wild card:
        if (this.path.charAt(this.path.length() - 1) == '*') {
            if (pathSize == (thisPathSize - 1) && (path.charAt(path.length() - 1) == '/')) {
                // Hack for making wildcards work with trailing slash
                pathList.add("");
                pathList.add("");
                pathSize += 2;
            }

            if (thisPathSize < pathSize) {
                for (int i = 0; i < thisPathSize; i++) {
                    String thisPathPart = thisPathList.get(i);
                    String pathPart = pathList.get(i);
                    if (thisPathPart.equals("*") &&
                            (i == thisPathSize - 1) &&
                            this.path.charAt(this.path.length() - 1) == '*') {
                        // wildcard match
                        return true;
                    }
                    if (thisPathPart.charAt(thisPathPart.length() - 1) != ':'
                            && !thisPathPart.equals(pathPart)
                            && !(thisPathPart.length() == 1 && thisPathPart.charAt(0) == '*')) {
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

    private boolean matchPathSameSize(List<String> thisPathList, List<String> pathList, int thisPathSize) {
        for (int i = 0; i < thisPathSize; i++) {
            String thisPathPart = thisPathList.get(i);
            String pathPart = pathList.get(i);

            if ((i == thisPathSize - 1) &&
                    (thisPathPart.length() == 1 && thisPathPart.charAt(0) == '*' &&
                            this.path.charAt(this.path.length() - 1) == '*')) {
                // wildcard match
                return true;
            }

            if (thisPathPart.charAt(0) != ':'
                    && !thisPathPart.equals(pathPart)
                    && !(thisPathPart.length() == 1 && thisPathPart.charAt(0) == '*')) {
                return false;
            }
        }
        // All parts matched
        return true;
    }

    public String toString() {
        return httpMethod.name() + ", " + path + ", " + target;
    }
}