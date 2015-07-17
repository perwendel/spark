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
package spark.webserver;

import java.util.Objects;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

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
	try {
	    Object handler = handlerClass.newInstance();
	    return new SparkWebSocketCreator(handler);
	} catch (InstantiationException | IllegalAccessException ex) {
	    throw new RuntimeException( "Could not instantiate websocket handler", ex);
	}
    }

    private static class SparkWebSocketCreator implements WebSocketCreator {
	private final Object handler;

	private SparkWebSocketCreator(Object handler) {
	    this.handler = Objects.requireNonNull(handler, "handler cannot be null");
	}

	@Override
	public Object createWebSocket(ServletUpgradeRequest request,
		ServletUpgradeResponse response) {
	    return handler;
	}
    }
}
