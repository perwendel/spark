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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.ThreadPool;

import spark.ExceptionMapper;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServerFactory;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Creates instances of embedded jetty containers.
 */
public class EmbeddedJettyFactory implements EmbeddedServerFactory {
    private final ConfigurableJettyServerFactory serverFactory;
    private final List<Handler> handlersBeforeRouteHandler = new ArrayList<>();
    private ThreadPool threadPool;
    private boolean httpOnly = true;

    public EmbeddedJettyFactory() {
        this(new JettyServer());
    }

    public EmbeddedJettyFactory(JettyServerFactory serverFactory) {
        this.serverFactory = new ConfigurableJettyServerFactory(serverFactory);
    }

    public EmbeddedServer create(Routes routeMatcher,
                                 StaticFilesConfiguration staticFilesConfiguration,
                                 ExceptionMapper exceptionMapper,
                                 boolean hasMultipleHandler) {
        Handler handler = getHandler(routeMatcher, staticFilesConfiguration, exceptionMapper, hasMultipleHandler);

        return new EmbeddedJettyServer(serverFactory, handler).withThreadPool(threadPool);
    }

    private Handler getHandler(Routes routeMatcher,
                               StaticFilesConfiguration staticFilesConfiguration,
                               ExceptionMapper exceptionMapper,
                               boolean hasMultipleHandler) {
        MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, exceptionMapper, false, hasMultipleHandler);
        matcherFilter.init(null);

        JettyHandler handler = new JettyHandler(matcherFilter);
        handler.getSessionCookieConfig().setHttpOnly(httpOnly);

        if (handlersBeforeRouteHandler.isEmpty()) {
            return handler;
        } else {
            Handler[] handlers = handlersBeforeRouteHandler.toArray(new Handler[0]);
            HandlerList handlerList = new HandlerList(handlers);
            handlerList.addHandler(handler);
            return handlerList;
        }
    }

    /**
     * Sets optional thread pool for jetty server.
     * This is useful for overriding the default thread pool behaviour for
     * example io.dropwizard.metrics.jetty9.InstrumentedQueuedThreadPool.
     *
     * @param threadPool thread pool
     * @return Builder pattern - returns this instance
     */
    public EmbeddedJettyFactory withThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
        return this;
    }

    /**
     * Marks or unmarks the session tracking cookies as <i>HttpOnly</i>.
     * For details see: {@link javax.servlet.SessionCookieConfig#setHttpOnly(boolean)}
     *
     * @param httpOnly true if the session tracking cookies shall be marked as <i>HttpOnly</i>, false otherwise
     * @return Builder pattern - returns this instance
     */
    public EmbeddedJettyFactory withHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    /**
     * Adds a handler to the list of handlers called by Jetty.
     * This method may be called multiple times to add more than one handler.
     * The handlers will be called in the order they have been added here.
     * The {@link JettyHandler} which handles the routes will be placed at the end of these handlers
     *
     * @param handler the handler.
     * @return Builder pattern - returns this instance
     */
    public EmbeddedJettyFactory withHandlerBeforeRouteHandler(Handler handler) {
        this.handlersBeforeRouteHandler.add(handler);
        return this;
    }

    /**
     * Adds an attribute to be applied on a newly created Jetty Server.
     * See {@link Server#setAttribute(String, Object)} for a detailed description.
     *
     * @param name      the name of the attribute.
     * @param attribute the value of the attribute.
     * @return Builder pattern - returns this instance
     */
    public EmbeddedJettyFactory withJettyServerAttribute(String name, Object attribute) {
        this.serverFactory.setServerAttribute(name, attribute);
        return this;
    }

    /**
     * Registers a callback which will be executed after a new Jetty Server has been created.
     * Only one callback can be registered. If this method is called multiple times only the very last
     * callback will be executed.
     *
     * @param configurator a callback which is passed the newly created server instance. May be {@code null}.
     * @return Builder pattern - returns this instance
     */
    public EmbeddedJettyFactory withJettyServerConfigurator(Consumer<Server> configurator) {
        this.serverFactory.setServerConfigurator(configurator);
        return this;
    }

    /**
     * Wrapper class around a JettyServerFactory which allows to configure the newly created server instance.
     */
    private static class ConfigurableJettyServerFactory implements JettyServerFactory {
        private final JettyServerFactory factory;
        private final Map<String, Object> serverAttributes = new HashMap<>();

        private Consumer<Server> configurator = null;

        private ConfigurableJettyServerFactory(JettyServerFactory factory) {
            this.factory = factory;
        }

        @Override
        public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
            final Server server = factory.create(maxThreads, minThreads, threadTimeoutMillis);
            return configureServer(server);
        }

        @Override
        public Server create(ThreadPool threadPool) {
            final Server server = factory.create(threadPool);
            return configureServer(server);
        }

        private Server configureServer(Server server) {
            for (Map.Entry<String, Object> entry : serverAttributes.entrySet()) {
                server.setAttribute(entry.getKey(), entry.getValue());
            }
            if (configurator != null) {
                configurator.accept(server);
            }
            return server;
        }

        private void setServerAttribute(String name, Object attribute) {
            serverAttributes.put(name, attribute);
        }

        private void setServerConfigurator(Consumer<Server> configurator) {
            this.configurator = configurator;
        }

    }
}
