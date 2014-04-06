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
package sparkj8c.examples.simple;

import static spark.SparkJ8C.get;
import static spark.SparkJ8C.post;

/**
 * A simple example just showing some basic functionality
 *
 * @author Per Wendel
 */
public class SimpleExample {

    public static void main(String[] args) {

        //  setPort(5678); <- Uncomment if you wan't spark to listen on a port different than 4567.

        get("/hello", it -> "Hello World!");

        post("/hello", it -> "Hello World: " + it.requestBody());

        get("/private", it -> {
            it.status(401);
            return "Go Away!!!";
        });

        get("/users/:name", it -> "Selected user: " + it.params(":name"));

        get("/news/:section", it -> {
            it.type("text/xml");
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + it.params("section") + "</news>";
        });

        get("/protected", it -> {
            it.halt(403, "I don't think so!!!");
            return null;
        });

        get("/redirect", it -> {
            it.redirect("/news/world");
            return null;
        });

        get("/", it -> "root");
    }
}
