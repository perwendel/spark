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
package spark.examples.sugar;

/**
 * Shows how Spark can be extended for creating some syntactic sugar.
 */
public class SugarExample {

    public static void main(String[] args) {

        http.get("/hi", () -> "Hi!");
        http.get("/hello", (q, a) -> "Hello!");
    }

}
