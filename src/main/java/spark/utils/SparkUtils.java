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

    private SparkUtils() {
    }

    public static List<String> convertRouteToList(String route) {
        List<String> path = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        char[] chars = route.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '/') {
                if (builder.length() > 0) {
                    path.add(builder.toString());
                    builder.setLength(0);
                }
            } else {
                builder.append(c);
            }
        }
        if (builder.length() > 0) {
            path.add(builder.toString());
        }
        return path;
    }

    public static boolean isParam(String routePart) {
        return routePart.charAt(0) == ':';
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }

}
