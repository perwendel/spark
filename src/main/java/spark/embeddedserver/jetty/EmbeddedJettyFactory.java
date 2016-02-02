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
package spark.embeddedserver.jetty;

import spark.route.RouteMatcherFactory;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.http.matching.MatcherFilter;

/**
 * Creates instances of embedded jetty containers.
 */
public class EmbeddedJettyFactory implements EmbeddedServerFactory {

    public EmbeddedServer create(boolean hasMultipleHandler) {
        MatcherFilter matcherFilter = new MatcherFilter(RouteMatcherFactory.get(), false, hasMultipleHandler);
        matcherFilter.init(null);

        JettyHandler handler = new JettyHandler(matcherFilter);
        return new EmbeddedJettyServer(handler);
    }

}
