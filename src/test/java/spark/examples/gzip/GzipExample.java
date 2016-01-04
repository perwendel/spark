/*
 * Copyright 2015 - Per Wendel
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
package spark.examples.gzip;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Example showing off the different GZIP features in Spark.
 */
public class GzipExample {

    public static final String FO_SHIZZY = "Fo shizzy";
    public static final String CONTENT = "the content that will be compressed/decompressed";

    private static final String PATH = "/zipped";

    public static void main(String[] args) throws Exception {
        addStaticFileLocation();
        addRoutes();
        awaitInitialization();

        String response = getAndDecompress();
        System.out.println("response = " + response);
        System.exit(0);
    }

    public static void addRoutes() {
        get("/hello", (q, a) -> FO_SHIZZY);
        get(PATH, (req, resp) -> {
            resp.header("Content-Encoding", "gzip");
            return CONTENT;
        });
    }

    public static void addStaticFileLocation() {
        staticFileLocation("/public");
    }

    public static String getAndDecompress() throws Exception {
        return GzipClient.getAndDecompress("http://localhost:4567" + PATH);
    }
}
