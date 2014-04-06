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
package sparkj8c.examples.filter;

import static spark.SparkJ8C.after;
import static spark.SparkJ8C.get;

/**
 * Example showing the use of attributes
 *
 * @author Per Wendel
 */
public class FilterExampleAttributes {

    public static void main(String[] args) {
        get("/hi", it -> {
            it.attribute("foo", "bar");
            return null;
        });

        after("/hi", it -> {
            for (String attr : it.attributes()) {
                System.out.println("attr: " + attr);
            }
        });

        after("/hi", it -> {
            Object foo = it.attribute("foo");
            it.body(asXml("foo", foo));
        });
    }

    private static String asXml(String name, Object value) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + name +">" + value + "</"+ name + ">";
    }
}
