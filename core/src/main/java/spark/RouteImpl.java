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
package spark;


import spark.utils.Wrapper;

/**
 * RouteImpl is created from a path, acceptType and Route. This is encapsulate the information needed in the route
 * matcher in a single container.
 *
 * @author Per Wendel
 */
public abstract class RouteImpl implements Route, Wrapper {
    static final String DEFAULT_ACCEPT_TYPE = "*/*";

    private String path;
    private String acceptType;
    private Object delegate;

    /**
     * Prefix the path (used for {@link Service#path})
     *
     * @param prefix the prefix
     * @return itself for easy chaining
     */
    public RouteImpl withPrefix(String prefix) {
        this.path = prefix + this.path;
        return this;
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    public static RouteImpl create(final String path, final Route route) {
        return create(path, DEFAULT_ACCEPT_TYPE, route);
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      the route
     * @return the wrapped route
     */
    public static RouteImpl create(final String path, String acceptType, final Route route) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        return new RouteImpl(path, acceptType, route) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return route.handle(request, response);
            }
        };
    }

    /**
     * Constructor
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     */
    protected RouteImpl(String path) {
        this(path, DEFAULT_ACCEPT_TYPE);
    }

    /**
     * Constructor
     *
     * @param path       The route path which is used for matching. (e.g. /hello, users/:name)
     * @param acceptType The accept type which is used for matching.
     */
    protected RouteImpl(String path, String acceptType) {
        this.path = path;
        this.acceptType = acceptType;
    }

    /**
     * Constructor
     *
     * @param path       The route path which is used for matching. (e.g. /hello, users/:name)
     * @param acceptType The accept type which is used for matching.
     * @param route      The route used to create the route implementation
     */
    protected RouteImpl(String path, String acceptType, Object route) {
        this(path, acceptType);
        this.delegate = route;
    }

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return The content to be set in the response
     * @throws java.lang.Exception when handle fails
     */
    public abstract Object handle(Request request, Response response) throws Exception;

    /**
     * This method should render the given element into something that can be send through Response element.
     * By default this method returns the result of calling toString method in given element, but can be overridden.
     *
     * @param element to be rendered.
     * @return body content.
     * @throws java.lang.Exception when render fails
     */
    public Object render(Object element) throws Exception {
        if (element != null) {
            return element;
        } else {
            return null;
        }
    }

    /**
     * @return the accept type
     */
    public String getAcceptType() {
        return acceptType;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @return the route used to create the route implementation
     */
    @Override
    public Object delegate() {
        return this.delegate;
    }

}
