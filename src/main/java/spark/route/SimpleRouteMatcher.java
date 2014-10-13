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

import java.util.*;

import com.augustl.pathtravelagent.MutableSynchronizedPathTravelAgent;
import com.augustl.pathtravelagent.Route;
import com.augustl.pathtravelagent.RouteStringBuilder;
import spark.utils.MimeParse;

/**
 * Simple route matcher that is supposed to work exactly as Sinatra's
 *
 * @author Per Wendel
 */
public class SimpleRouteMatcher {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleRouteMatcher.class);
    private static final char SINGLE_QUOTE = '\'';
    private final MutableSynchronizedPathTravelAgent<SparkRequest, SparkResponse> pta = new MutableSynchronizedPathTravelAgent<>();
    private final HashMap<String, SparkRequestHandler> handlerByPath = new HashMap<>();

    /**
     * Constructor
     */
    public SimpleRouteMatcher() {
    }

    /**
     * Parse and validates a route and adds it
     *
     * @param route      the route path
     * @param acceptType the accept type
     * @param target     the invocation target
     */
    public void parseValidateAddRoute(String route, String acceptType, Object target) {
        try {
            int singleQuoteIndex = route.indexOf(SINGLE_QUOTE);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase(); // NOSONAR
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim(); // NOSONAR

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
            addRoute(method, url, acceptType, target);
        } catch (Exception e) {
            LOG.error("The @Route value: " + route + " is not in the correct format", e);
        }
    }

    /**
     * finds target for a requested route
     *
     * @param httpMethod the http method
     * @param path       the path
     * @param acceptType the accept type
     * @return the target
     */
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        SparkResponse res = pta.match(new SparkRequest(httpMethod, path));
        if (res == null) {
            return null;
        } else {
            Object target = findTargetWithGivenAcceptType(res, acceptType);
            if (target == null) {
                return null;
            } else {
                return new RouteMatch(httpMethod, target, res.getRouteSourceUrl(), path, acceptType);
            }
        }
    }

    /**
     * Finds multiple targets for a requested route.
     *
     * @param httpMethod the http method
     * @param path       the route path
     * @param acceptType the accept type
     * @return the targets
     */
    public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod, String path, String acceptType) {
        List<RouteMatch> matchSet = new ArrayList<>();

        SparkResponse res = pta.match(new SparkRequest(httpMethod, path));
        if (res != null) {
            Set<String> acceptedMimeTypes = res.getAcceptedMimeTypes();

            if (acceptType == null) {
                matchSet.add(new RouteMatch(httpMethod, res.getDefaultTarget(), res.getRouteSourceUrl(), path, null));
            } else {
                for (String acceptedMimeType : acceptedMimeTypes) {
                    String bestMatch = MimeParse.bestMatch(Arrays.asList(acceptedMimeType), acceptType);
                    if (routeWithGivenAcceptType(bestMatch)) {
                        matchSet.add(new RouteMatch(httpMethod, res.getTargetForAcceptType(bestMatch), res.getRouteSourceUrl(), path, acceptType));
                    }
                }
            }
        }

        return matchSet;
    }

    /**
     * ¨Clear all routes
     */
    public void clearRoutes() {
        pta.clear();
        this.handlerByPath.clear();
    }

    //////////////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////////////

    private void addRoute(HttpMethod method, String url, String acceptedType, Object target) {
        LOG.debug("Adds route: " + method.name() + ", " + url + ", " + target);

        SparkRequestHandler handler = handlerByPath.get(url);
        if (handler == null) {
            handler = new SparkRequestHandler(url);
            handler.addMethodAndAcceptHandler(method, acceptedType, target);
            handlerByPath.put(url, handler);
            Route<SparkRequest, SparkResponse> route =
                new RouteStringBuilder<SparkRequest, SparkResponse>(":").build(url, handler);
            pta.addRoute(route);
        } else {
            handler.addMethodAndAcceptHandler(method, acceptedType, target);
        }
    }

    private boolean routeWithGivenAcceptType(String bestMatch) {
        return !MimeParse.NO_MIME_TYPE.equals(bestMatch);
    }

    // TODO: I believe this feature has impacted performance. Optimization?
    private Object findTargetWithGivenAcceptType(SparkResponse res, String acceptType) {
        if (acceptType != null) {
            Set<String> acceptedMimeTypes = res.getAcceptedMimeTypes();

            String bestMatch = MimeParse.bestMatch(acceptedMimeTypes, acceptType);

            if (routeWithGivenAcceptType(bestMatch)) {
                return res.getTargetForAcceptType(bestMatch);
            } else {
                return null;
            }
        } else {
            return res.getDefaultTarget();
        }
    }

}
