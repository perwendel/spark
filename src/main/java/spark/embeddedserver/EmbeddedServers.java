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

import java.util.HashMap;
import java.util.Map;

import spark.embeddedserver.jetty.EmbeddedJettyFactory;

/**
 * Holds and uses the factories for creating different Embedded servers.
 */
public class EmbeddedServers {

    // Default alternatives.
    public enum Identifiers {
        JETTY
    }

    private static Map<Object, EmbeddedServerFactory> factories = new HashMap<>();

    public static Identifiers defaultIdentifier() {
        return Identifiers.JETTY;
    }

    /**
     * Creates an embedded server of type corresponding to the provided identifier.
     */
    public static EmbeddedServer create(Object identifier, boolean multipleHandlers) {
        if ( factories.isEmpty() ){
            addAll();
        }

        EmbeddedServerFactory factory = factories.get(identifier);

        if (factory != null) {
            return factory.create(multipleHandlers);
        } else {
            throw new RuntimeException("No embedded server matching the identifier");
        }
    }

    private static void addAll() {
        add(Identifiers.JETTY, new EmbeddedJettyFactory());
    }

    /**
     * Adds an Embedded server factory for the provided identifier.
     */
    public static void add(Object identifier, EmbeddedServerFactory factory) {
        factories.put(identifier, factory);
    }
}
