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
package spark.examples.simple;

import spark.util.SparkTestUtil;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.secure;

/**
 * A simple example just showing some basic functionality.
 *
 * @author Peter Nicholls, based on (practically identical to in fact)
 *         {@link spark.examples.simple.SimpleExample} by Per Wendel
 */
public class SimpleSecureExample {

    public static void main(String[] args) {

        // port(5678); <- Uncomment this if you want spark to listen on a
        // port different than 4567.

        secure(
                SparkTestUtil.getKeyStoreLocation(),
                SparkTestUtil.getKeystorePassword(), null, null);

        get("/hello", (request, response) -> "Hello Secure World!");

        post("/hello", (request, response) -> "Hello Secure World: " + request.body());

        get("/private", (request, response) -> {
            response.status(401);
            return "Go Away!!!";
        });

        get("/users/:name", (request, response) -> "Selected user: " + request.params(":name"));

        get("/news/:section", (request, response) -> {
            response.type("text/xml");
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>"
                    + request.params("section") + "</news>";
        });

        get("/protected", (request, response) -> {
            halt(403, "I don't think so!!!");
            return null;
        });

        get("/redirect", (request, response) -> {
            response.redirect("/news/world");
            return null;
        });

        get("/", (request, response) -> "root");
    }
}
