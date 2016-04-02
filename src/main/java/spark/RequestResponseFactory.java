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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.routematch.RouteMatch;

public final class RequestResponseFactory {

    private RequestResponseFactory() {
    }

    public static Request create(RouteMatch match, HttpServletRequest request) {
        return new Request(match, request);
    }

    public static Response create(HttpServletResponse response) {
        return new Response(response);
    }

}
