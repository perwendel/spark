
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the custom error pages. A page can be defined as a String or a Route.
 * Note that this class is always used statically therefore custom error pages will
 * be shared between different instances of the Service class.
 */
public class CustomErrorPages {

    private static final Logger LOG = LoggerFactory.getLogger(CustomErrorPages.class);
    public static final String NOT_FOUND = "<html><body><h2>404 Not found</h2></body></html>";
    public static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Server Error</h2></body></html>";

    /**
     * Verifies that a custom error page exists for the given status code
     * @param status
     * @return true if error page exists
     */
    public static boolean existsFor(int status) {
        return CustomErrorPages.getInstance().customPages.containsKey(status);
    }

    /**
     * Gets the custom error page for a given status code.  If the custom
     * error page is a route, the output of its handle method is returned.
     * If the custom error page is a String, it is returned as an Object.  
     * @param status
     * @param request
     * @param response
     * @return Object representing the custom error page
     */
    public static Object getFor(int status, Request request, Response response) {

        Object customRenderer = CustomErrorPages.getInstance().customPages.get(status);
        Object customPage = CustomErrorPages.getInstance().getDefaultFor(status);

        if (customRenderer instanceof String) {
            customPage = customRenderer;
        } else if (customRenderer instanceof Route) {
            try {
                customPage = ((Route) customRenderer).handle(request, response);
            } catch (Exception e) {
             // The custom page renderer is causing an internal server error.  Log exception as a warning and use default page instead
                LOG.warn("Custom error page handler for status code {} has thrown an exception: {}. Using default page instead.", status, e.getMessage());
            }
        }

        return customPage;
    }

    /**
     * Returns the default error page for a given status code.
     * Guaranteed to never be null.
     * @param status
     * @return String representation of the default error page.
     */
    public String getDefaultFor(int status){
        String defaultPage = defaultPages.get(status);
        return (defaultPage != null) ? defaultPage : "<html><body><h2>HTTP Status " + status + "</h2></body></html>";
    }
    
    /**
     * Add a custom error page as a String
     * @param status
     * @param page
     */
    static void add(int status, String page) {
        CustomErrorPages.getInstance().customPages.put(status, page);
    }

    /**
     * Add a custom error page as a Route handler
     * @param status
     * @param route
     */
    static void add(int status, Route route) {
        CustomErrorPages.getInstance().customPages.put(status, route);
    }

    // Private stuff

    private final HashMap<Integer, Object> customPages;
    private final HashMap<Integer, String> defaultPages;

    private CustomErrorPages() {
        customPages = new HashMap<>();
        defaultPages = new HashMap<>();
        defaultPages.put(404, NOT_FOUND);
        defaultPages.put(500, INTERNAL_ERROR);
    }

    private static class SingletonHolder {
        private static final CustomErrorPages INSTANCE = new CustomErrorPages();
    }

    private static CustomErrorPages getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
