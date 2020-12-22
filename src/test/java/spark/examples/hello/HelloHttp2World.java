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
package spark.examples.hello;

import static spark.Spark.get;
import static spark.Spark.http2;

/**
 * This will start a server with HTTP 1.1 and HTTP2 clear
 * By default it will do HTTP 1.1 but you can upgrade for HTTP 2
 * or use HTTP 2 directly.
 * To test the upgrade you can run: "nghttp -vu http://127.0.0.1:8080/hello"
 * To test the direct use of http2: "nghttp -v http://127.0.0.1:8080/hello"
 **/
 public class HelloHttp2World {

    public static void main(String[] args) {
        http2();
        get("/hello", (request, response) -> "Hello World!");
    }
}
