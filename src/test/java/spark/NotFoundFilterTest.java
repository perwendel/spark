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
package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.GenericSecureIntegrationTest.testUtil;
import static spark.Spark.get;
import static spark.Spark.notFound;
import spark.util.SparkTestUtil;

/**
 *
 * @author fcambarieri
 */
public class NotFoundFilterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundFilterTest.class);

  private static final String JSON_NOT_FOUND_BRO = "{'msg':'Not found bro'}";

  @AfterClass
  public static void tearDown() {
    Spark.stop();
  }

  @BeforeClass
  public static void setup() {
    testUtil = new SparkTestUtil(4567);

    get("/hi", (request, response) -> {
      return "Hello World!";
    });

    notFound((Request request, Response response) -> {
      response.header("My-Header", "WTF");
      response.header("Content-Type", "application/json");
      response.body(JSON_NOT_FOUND_BRO);
      response.status(404);
    });

    Spark.awaitInitialization();
  }

  @Test
  public void testGetNotFoundMapper() throws Exception {
    SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/not_mapping_uri", null);
    LOGGER.info("Response status {} body {}", response.status, response.body);
    Assert.assertEquals(JSON_NOT_FOUND_BRO, response.body);
    Assert.assertEquals(404, response.status);

  }
  
  @Test
  public void testPostNotFoundMapper() throws Exception {
    SparkTestUtil.UrlResponse response = testUtil.doMethod("POST", "/not_mapping_uri", "msg=hola", "text/html");
    LOGGER.info("Response status {} body {}", response.status, response.body);
    Assert.assertEquals(JSON_NOT_FOUND_BRO, response.body);
    Assert.assertEquals(404, response.status);
  }
  
  @Test
  public void testPutNotFoundMapper() throws Exception {
    SparkTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/not_mapping_uri", "msg=hola", "text/html");
    LOGGER.info("Response status {} body {}", response.status, response.body);
    Assert.assertEquals(JSON_NOT_FOUND_BRO, response.body);
    Assert.assertEquals(404, response.status);
  }
  
  @Test
  public void testDeleteNotFoundMapper() throws Exception {
    SparkTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/not_mapping_uri", null);
    LOGGER.info("Response status {} body {}", response.status, response.body);
    Assert.assertEquals(JSON_NOT_FOUND_BRO, response.body);
    Assert.assertEquals(404, response.status);
  }
}
