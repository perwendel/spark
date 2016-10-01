/*
 * Copyright 2016 fcambarieri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.examples.filter;

import static spark.Spark.get;
import static spark.Spark.notFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fcambarieri
 */
public class FilterExampleNotFound {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilterExampleNotFound.class);
  
  public static void main(String[] args) {
    notFound((request, response) -> {
      LOGGER.info("not found");
      response.header("Content-Type", "application/json");
      response.body("{'error':'not found handler'}");
      response.status(404);
    });

    get("/hello", (request, response) -> {
      return "Hello World!";
    });
  }
}
