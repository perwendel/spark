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

import spark.*;

/**
 * A simple example just showing some basic functionality You'll need to provide
 * a JKS keystore as arg 0 and its password as arg 1.
 * 
 * @author Peter Nicholls, based on (practically identical to in fact)
 *         {@link spark.examples.simple.SimpleExample} by Per Wendel
 */
public class SimpleSecureExample {

    public static void main(String[] args) {

        // setPort(5678); <- Uncomment this if you want spark to listen on a
        // port different than 4567.

        SSLConfig sslConfig = new SSLConfig(args[0], args[1], null, null);
        SparkInstance spark = new SparkInstance(SparkInstance.SPARK_DEFAULT_IP, SparkInstance.SPARK_DEFAULT_PORT, sslConfig, null);

        spark.get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello Secure World!";
            }
        });

        spark.post(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello Secure World: " + request.body();
            }
        });

        spark.get(new Route("/private") {
            @Override
            public Object handle(Request request, Response response) {
                response.status(401);
                return "Go Away!!!";
            }
        });

        spark.get(new Route("/users/:name") {
            @Override
            public Object handle(Request request, Response response) {
                return "Selected user: " + request.params(":name");
            }
        });

        spark.get(new Route("/news/:section") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("text/xml");
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>"
                        + request.params("section") + "</news>";
            }
        });

        spark.get(new Route("/protected") {
            @Override
            public Object handle(Request request, Response response) {
                halt(403, "I don't think so!!!");
                return null;
            }
        });

        spark.get(new Route("/redirect") {
            @Override
            public Object handle(Request request, Response response) {
                response.redirect("/news/world");
                return null;
            }
        });

        spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return "root";
            }
        });

    }
}
