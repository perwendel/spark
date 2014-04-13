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

import java.util.regex.Pattern;

/**
 * Functionality used in both Route and Filter
 *
 * @author Per Wendel
 */
abstract class AbstractRoute {

    private String path;
    private Pattern pathPattern;
    private String acceptType;

    protected AbstractRoute(Pattern path, String acceptType) {
        this.pathPattern = path;
        this.acceptType = acceptType;
    }

    protected AbstractRoute(String path, String acceptType) {
        this.path = path;
        this.acceptType = acceptType;
    }

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public final void halt() {
        throw new HaltException();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public final void halt(int status) {
        throw new HaltException(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public final void halt(String body) {
        throw new HaltException(body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body The body content
     */
    public final void halt(int status, String body) {
        throw new HaltException(status, body);
    }

    public String getAcceptType() {
        return acceptType;
    }

    /**
     * Returns this route's path
     */
    protected String getPath() {
        return this.path;
    }

    protected Pattern getPathPattern () {
        return pathPattern;
    }

    public void pass () {}
    public void redirect () {}
    public void template (String aTemplate, javafx.util.Pair<String, ?>... aParams) {}

    public void template (String aTemplate, String aLayout, javafx.util.Pair<String, ?>... aParams) {}

}
