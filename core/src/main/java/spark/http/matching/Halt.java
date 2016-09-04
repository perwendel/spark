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

import javax.servlet.http.HttpServletResponse;

import spark.HaltException;

/**
 * Modifies the HTTP response and body based on the provided HaltException.
 */
public class Halt {

    /**
     * Modifies the HTTP response and body based on the provided HaltException.
     *
     * @param httpResponse The HTTP servlet response
     * @param body         The body content
     * @param halt         The halt exception object
     */
    public static void modify(HttpServletResponse httpResponse, Body body, HaltException halt) {

        httpResponse.setStatus(halt.statusCode());

        if (halt.body() != null) {
            body.set(halt.body());
        } else {
            body.set("");
        }
    }
}
