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
package spark.globalstate;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Holds the global information if Spark was run from an "external" web application server.
 */
public class ServletFlag {

    private static AtomicBoolean isRunningFromServlet = new AtomicBoolean(false);

    /**
     * Tells the system that Spark was run from an "external" web application server.
     */
    public static void runFromServlet() {
        isRunningFromServlet.set(true);
    }

    /**
     * @return true if Spark was run from an "external" web application server.
     */
    public static boolean isRunningFromServlet() {
        return isRunningFromServlet.get();
    }

}
