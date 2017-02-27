/*
 * Copyright 2016 - Per Wendel
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
package spark;

import java.util.HashMap;

/**
 * Holds the customer error pages.
 */
public class CustomErrorPages {

    public static final String NOT_FOUND = "<html><body><h2>404 Not found</h2></body></html>";
    public static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";

    public static boolean existsFor(int status) {
        return CustomErrorPages.getInstance().customPages.containsKey(status);
    }

    public static Object getFor(int status, Request request, Response response) {

        Object customRenderer = CustomErrorPages.getInstance().customPages.get(status);
        Object customPage;

        customPage = status == 404 ? NOT_FOUND : INTERNAL_ERROR;

        if (customRenderer instanceof String) {
            customPage = customRenderer;
        } else if (customRenderer instanceof Route) {
            try {
                customPage = ((Route) customRenderer).handle(request, response);
            } catch (Exception e) {
                // customPage is already set to default error so nothing needed here
            }
        }

        return customPage;
    }

    static void add(int status, String page) {
        CustomErrorPages.getInstance().customPages.put(status, page);
    }

    static void add(int status, Route route) {
        CustomErrorPages.getInstance().customPages.put(status, route);
    }

    // Private stuff

    private final HashMap<Integer, Object> customPages;

    private CustomErrorPages() {
        customPages = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final CustomErrorPages INSTANCE = new CustomErrorPages();
    }

    private static CustomErrorPages getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
