/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import static java.util.Objects.requireNonNull;

/**
 * Factory class to create {@link WebSocketCreator} implementations that
 * delegate to the given handler class.
 *
 * @author Ignasi Barrera
 */
public class WebSocketCreatorFactory {

    /**
     * Creates a {@link WebSocketCreator} that uses the given handler class for
     * the WebSocket connections.
     *
     * @param handlerClass The handler to use to manage WebSocket connections.
     * @return The WebSocketCreator.
     */
    public static WebSocketCreator create(Class<?> handlerClass) {
        validate(handlerClass);
        try {
            Object handler = handlerClass.newInstance();
            return new SparkWebSocketCreator(handler);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Could not instantiate websocket handler", ex);
        }
    }

    /**
     * Validates that the handler can actually handle the WebSocket connection.
     *
     * @param handlerClass The handler class to validate.
     * @throws IllegalArgumentException if the class is not a valid handler class.
     */
    private static void validate(Class<?> handlerClass) {
        boolean valid = WebSocketListener.class.isAssignableFrom(handlerClass)
                || handlerClass.isAnnotationPresent(WebSocket.class);
        if (!valid) {
            throw new IllegalArgumentException(
                    "WebSocket handler must implement 'WebSocketListener' or be annotated as '@WebSocket'");
        }
    }

    // Package protected to be visible to the unit tests
    static class SparkWebSocketCreator implements WebSocketCreator {
        private final Object handler;

        private SparkWebSocketCreator(Object handler) {
            this.handler = requireNonNull(handler, "handler cannot be null");
        }

        @Override
        public Object createWebSocket(ServletUpgradeRequest request,
                                      ServletUpgradeResponse response) {
            return handler;
        }

        Object getHandler() {
            return handler;
        }
    }
}
