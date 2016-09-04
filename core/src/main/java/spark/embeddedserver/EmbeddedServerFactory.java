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
package spark.embeddedserver;

import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

/**
 * @author Per Wendel
 */
public interface EmbeddedServerFactory {

    /**
     * Creates an embedded server instance.
     *
     * @param routeMatcher The route matcher
     * @param staticFilesConfiguration The static files configuration object
     * @param hasMultipleHandler true if other handlers exist
     * @return the created instance
     */
    public EmbeddedServer create(Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler);
}
