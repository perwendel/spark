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
package spark.examples.multiple;

import spark.Service;

import static spark.Service.ignite;

/**
 * This example shows how to correctly use the Spark instance API which allows for multiple services.
 * The factory method 'service()' should be statically imported from the Spark class.
 */
public class MultipleServices {

    public static void main(String[] args) {
        igniteFirstService();
        igniteSecondService();
    }

    private static void igniteFirstService() {
        Service http = ignite(); // I give the variable the name 'http' for the code to make sense when adding routes.
        http.get("/hello", (q, a) -> "Hello World!");
    }

    private static void igniteSecondService() {
        Service http = ignite()
                .port(1234)
                .staticFileLocation("/public")
                .threadPool(40);

        http.get("/hello", (q, a) -> "Hello World!");

        http.redirect.any("/hi", "/hello");
    }

}
