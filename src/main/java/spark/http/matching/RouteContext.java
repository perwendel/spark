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
package spark.http.matching;

import javax.servlet.http.HttpServletRequest;

import spark.Response;
import spark.route.*;
import spark.route.Routes;

/**
 * Holds the parameters needed in the Before filters, Routes and After filters execution.
 */
final class RouteContext {

    /**
     * Creates a RouteContext
     */
    static RouteContext create() {
        return new RouteContext();
    }

    private Routes routeMatcher;
    private HttpServletRequest httpRequest;
    private String uri;
    private String acceptType;
    private Body body;
    private RequestWrapper requestWrapper;
    private ResponseWrapper responseWrapper;
    private Response response;
    private HttpMethod httpMethod;

    private RouteContext() {
        // hidden
    }

    public Routes routeMatcher() {
        return routeMatcher;
    }

    public RouteContext withMatcher(spark.route.Routes routeMatcher) {
        this.routeMatcher = routeMatcher;
        return this;
    }

    public RouteContext withHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    public RouteContext withAcceptType(String acceptType) {
        this.acceptType = acceptType;
        return this;
    }

    public RouteContext withBody(Body body) {
        this.body = body;
        return this;
    }

    public RouteContext withRequestWrapper(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
        return this;
    }

    public RouteContext withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RouteContext withResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
        return this;
    }

    public RouteContext withResponse(Response response) {
        this.response = response;
        return this;
    }

    public RouteContext withHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public HttpServletRequest httpRequest() {
        return httpRequest;
    }

    public String uri() {
        return uri;
    }

    public String acceptType() {
        return acceptType;
    }

    public Body body() {
        return body;
    }

    public RequestWrapper requestWrapper() {
        return requestWrapper;
    }

    public ResponseWrapper responseWrapper() {
        return responseWrapper;
    }

    public Response response() {
        return response;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

}
