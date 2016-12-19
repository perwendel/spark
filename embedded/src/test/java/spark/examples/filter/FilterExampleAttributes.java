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
package spark.examples.filter;

import static spark.Spark.after;
import static spark.Spark.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example showing the use of attributes
 *
 * @author Per Wendel
 */
public class FilterExampleAttributes {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterExampleAttributes.class);

    public static void main(String[] args) {
        get("/hi", (request, response) -> {
            request.attribute("foo", "bar");
            return null;
        });

        after("/hi", (request, response) -> {
            for (String attr : request.attributes()) {
                LOGGER.info("attr: " + attr);
            }
        });

        after("/hi", (request, response) -> {
            String foo = request.attribute("foo");
            response.body(asXml("foo", foo));
        });
    }

    private static String asXml(String name, String value) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + name + ">" + value + "</" + name + ">";
    }

}
