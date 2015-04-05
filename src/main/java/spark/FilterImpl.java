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

import spark.utils.SparkUtils;


/**
 * A Filter is built up by a path (for url-matching) and the implementation of the 'handle' method.
 * When a request is made, if present, the matching routes 'handle' method is invoked.
 *
 * @author Per Wendel
 */
public abstract class FilterImpl implements Filter {

    private static final String DEFAUT_CONTENT_TYPE = "text/html";

    private String path;
    private String acceptType;

    /**
     * Constructs a filter that matches on everything
     */
    protected FilterImpl() {
        this(SparkUtils.ALL_PATHS);
    }

    /**
     * Constructor
     *
     * @param path The filter path which is used for matching. (e.g. /hello, users/:name)
     */
    protected FilterImpl(String path) {
        this(path, DEFAUT_CONTENT_TYPE);
    }

    protected FilterImpl(String path, String acceptType) {
        this.path = path;
        this.acceptType = acceptType;
    }

    /**
     * Invoked when a request is made on this filter's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     */
    public abstract void handle(Request request, Response response) throws Exception;

    public String getAcceptType() {
        return acceptType;
    }

    /**
     * Returns this route's path
     */
    String getPath() {
        return this.path;
    }

}