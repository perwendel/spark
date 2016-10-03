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

import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

/**
 * Holds and uses the factories for creating different Embedded servers.
 */
public class EmbeddedServers {

    // Default alternatives.
    public enum Identifiers {
        JETTY
    }

    private enum Factories {
        JETTY_FACTORY("spark.embeddedserver.jetty.EmbeddedJettyFactory");

        private final Class<EmbeddedServerFactory> factoryClass;

        @SuppressWarnings("unchecked")
        private Factories(String factoryClassName) {
            try {
                this.factoryClass = (Class<EmbeddedServerFactory>) Class.forName(factoryClassName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Factory class not found: " + factoryClassName);
            }
        }

        private static Factories valueOf(Identifiers identifier) {
            switch (identifier) {
            case JETTY: return JETTY_FACTORY;
            default:	throw new IllegalArgumentException("No factory defined for: " + identifier);
            }
        }

        private EmbeddedServerFactory instantiateFactory() {
            try {
                return factoryClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Unable to instantiate factory: " + factoryClass.getName(), e);
            }
        }
    }

    private static Map<Object, EmbeddedServerFactory> factories = new HashMap<>();

    public static void initialize() {
        for (Identifiers identifier : Identifiers.values()) {
            add(identifier, Factories.valueOf(identifier).instantiateFactory());
        }
    }

    public static Identifiers defaultIdentifier() {
        return Identifiers.JETTY;
    }

    /**
     * Creates an embedded server of type corresponding to the provided identifier.
     *
     * @param identifier               the identifier
     * @param routeMatcher             the route matcher
     * @param staticFilesConfiguration the static files configuration object
     * @param multipleHandlers         true if other handlers exist
     * @return the created EmbeddedServer object
     */
    public static EmbeddedServer create(Object identifier,
            Routes routeMatcher,
            StaticFilesConfiguration staticFilesConfiguration,
            boolean multipleHandlers) {

        EmbeddedServerFactory factory = factories.get(identifier);

        if (factory != null) {
            return factory.create(routeMatcher, staticFilesConfiguration, multipleHandlers);
        } else {
            throw new RuntimeException("No embedded server matching the identifier");
        }
    }

    /**
     * Adds an Embedded server factory for the provided identifier.
     *
     * @param identifier the identifier
     * @param factory    the factory
     */
    public static void add(Object identifier, EmbeddedServerFactory factory) {
        factories.put(identifier, factory);
    }

}
