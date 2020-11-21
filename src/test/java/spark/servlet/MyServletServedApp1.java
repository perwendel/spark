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

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;

public class MyServletServedApp1 implements SparkApplication {

    @Override
    public void init() {
        get("/servlet/", (request, response) -> "Hello Root!");

        post("/servlet/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        after("/servlet/hi", (request, response) -> response.header("after", "foobar"));

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }
}
