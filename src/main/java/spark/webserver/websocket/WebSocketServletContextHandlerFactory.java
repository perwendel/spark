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
package spark.webserver.websocket;

import java.util.Map;
import java.util.Optional;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.server.pathmap.ServletPathSpec;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates websocket servlet context handlers.
 */
public class WebSocketServletContextHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServletContextHandlerFactory.class);

    /**
     * Creates a new websocket servlet context handler.
     *
     * @param webSocketHandlers          webSocketHandlers
     * @param webSocketIdleTimeoutMillis webSocketIdleTimeoutMillis
     * @return a new websocket servlet context handler or 'null' if creation failed.
     */
    public static ServletContextHandler create(Map<String, Class<?>> webSocketHandlers,
                                               Optional<Integer> webSocketIdleTimeoutMillis) {
        ServletContextHandler webSocketServletContextHandler = null;
        if (webSocketHandlers != null) {
            try {
                webSocketServletContextHandler = new ServletContextHandler(null, "/", true, false);
                WebSocketUpgradeFilter webSocketUpgradeFilter = WebSocketUpgradeFilter.configureContext(webSocketServletContextHandler);
                if (webSocketIdleTimeoutMillis.isPresent()) {
                    webSocketUpgradeFilter.getFactory().getPolicy().setIdleTimeout(webSocketIdleTimeoutMillis.get());
                }
                for (String path : webSocketHandlers.keySet()) {
                    WebSocketCreator webSocketCreator = WebSocketCreatorFactory.create(webSocketHandlers.get(path));
                    webSocketUpgradeFilter.addMapping(new ServletPathSpec(path), webSocketCreator);
                }
            } catch (Exception ex) {
                logger.error("creation of websocket context handler failed.", ex);
                webSocketServletContextHandler = null;
            }
        }
        return webSocketServletContextHandler;
    }

}
