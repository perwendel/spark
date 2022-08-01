/*
 * Copyright 2016- Per Wendel
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
package spark.servlet;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;

public class MyServletServedApp2 implements SparkApplication {
    static final String EXTERNAL_FILE = "servletServed2.html";

    @Override
    public synchronized void init() {
        before("/servlet/protected/*", (request, response) -> halt(401, "Go Away!"));

        get("/servlet/hi", (request, response) -> "Hello World!");

        get("/servlet/:param", (request, response) -> "echo: " + request.params(":param"));
    }
}
