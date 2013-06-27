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
 * The main building block of a Spark application is a set of routes. A route is
 * made up of three simple pieces:
 * <p/>
 * <ul>
 * <li>A verb (get, post, put, delete, head, trace, connect, options)</li>
 * <li>A path (/hello, /users/:name)</li>
 * <li>A callback ( handle(Request request, Response response) )</li>
 * </ul>
 * <p/>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * Spark.get(new Route("/hello") {
 *    public Object handle(Request request, Response response) {
 *       return "Hello World!";
 *    }
 * });
 * </pre>
 * <p/>
 * <code>
 * <p/>
 * </code>
 *
 * @author Per Wendel
 */
public final class Spark {
    /*
     * TODO: discover new TODOs.
     * 
     * 
     * TODO: Make available as maven dependency, upload on repo etc... 
     * TODO: Add *, splat possibility 
     * TODO: Add validation of routes, invalid characters and stuff, also validate parameters, check static, ONGOING
     * 
     * TODO: Javadoc
     * 
     * TODO: Create maven archetype, "ONGOING" 
     * TODO: Add cache-control helpers
     * 
     * advanced TODO list: 
     * TODO: Add regexp URIs
     * 
     * Ongoing
     * 
     * Done 
     * TODO: Routes are matched in the order they are defined. The rirst route that matches the request is invoked. ??? 
     * TODO: Before method for filters...check sinatra page 
     * TODO: Setting Headers 
     * TODO: Do we want get-prefixes for all *getters* or do we want a more ruby like approach???
     * (Maybe have two choices?) 
     * TODO: Setting Body, Status Code 
     * TODO: Add possibility to set content type on return, DONE 
     * TODO: Add possibility to access HttpServletContext in method impl, DONE 
     * TODO: Redirect func in web context, DONE 
     * TODO: Refactor, extract interfaces, DONE 
     * TODO: Figure out a nice name, DONE - SPARK 
     * TODO: Add /uri/{param} possibility, DONE 
     * TODO: Tweak log4j config, DONE 
     * TODO: Query string in web context, DONE 
     * TODO: Add URI-param fetching from webcontext ie. ?param=value&param2=...etc, AND headers, DONE
     * TODO: sessions? (use session servlet context?) DONE
     */
}
