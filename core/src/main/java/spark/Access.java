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
package spark;

import spark.routematch.RouteMatch;

/**
 * Provides access to package protected methods. JUST FOR INTERNAL USE. NOT PART OF PUBLIC SPARK API.
 */
public final class Access {

    private Access() {
        // hidden
    }

    public static void changeMatch(Request request, RouteMatch match) {
        request.changeMatch(match);
    }

}
