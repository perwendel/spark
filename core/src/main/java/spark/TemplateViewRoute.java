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
 * A TemplateViewRoute is built up by a path.
 * TemplateViewRoute instead of returning the result of calling toString() as body, it returns the result of calling render method.
 * The primary purpose is provide a way to create generic and reusable components for rendering output using a Template Engine.
 * For example to render objects to html by using Freemarker template engine..
 *
 * @author alex
 */
@FunctionalInterface
public interface TemplateViewRoute {

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     *
     * @param request  The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * @return The content to be set in the response
     * @throws java.lang.Exception
     */
    ModelAndView handle(Request request, Response response) throws Exception;

}
