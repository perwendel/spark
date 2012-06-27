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


/**
 * A Route is built up by a path (for url-matching) and the implementation of the 'handle' method.
 * When a request is made, if present, the matching routes 'handle' method is invoked. The object
 * that is returned from 'handle' will be set to the response body (toString()).
 *
 * @author Per Wendel
 */
public abstract class Route extends AbstractRoot {

    private String path;
    
    /**
     * Constructor
     * 
     * @param path The route path which is used for matching. (e.g. /hello, users/:name) 
     */
    protected Route(String path) {
        this.path = path;
    }
    
    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     * 
     * @param request The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * 
     * @return The content to be set in the response
     */
    public abstract Object handle(Request request, Response response);

    /**
     * Returns this route's path
     */
    String getPath() {
        return this.path;
    }
    
}
