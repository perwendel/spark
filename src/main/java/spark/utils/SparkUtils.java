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
package spark.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Some utility methods
 *
 * @author Per Wendel
 */
public final class SparkUtils {

    public static final String ALL_PATHS = "+/*paths";
    /**
     * The default value for the path matcher and follows the standard convention.
     * Path parameters can be defined as {@code :parameter}.
     */
    public static final String DEFAULT_PATTERN = ":.+";

    /**
     * Matcher for the curly bracket notation. This pattern is used by many
     * popular frameworks like Spring MVC or OkHttp. The expression matches
     * strings like {@code {id}, {value}} or {@code {pathVariable}}.
     */
    public static final String CURLY_BRACKET_MATCHER = "\\{.+\\}";

    private static String regex = DEFAULT_PATTERN;

    private SparkUtils() {
    }


    /**
     * Set the regular expression for the path mather.
     * @param regex regular expression for the path variables.
     */
    public static void paramMatcher(String regex) {
        SparkUtils.regex = regex;
    }

    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }

    public static boolean isParam(String routePart) {
        return routePart.matches(regex);
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }

}
