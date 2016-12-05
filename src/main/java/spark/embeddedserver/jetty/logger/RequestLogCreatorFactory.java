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
package spark.embeddedserver.jetty.logger;

import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import spark.embeddedserver.jetty.websocket.WebSocketHandlerWrapper;

import static java.util.Objects.requireNonNull;

/**
 * Factory class to create {@link WebSocketCreator} implementations that
 * delegate to the given handler class.
 *
 * @author Ignasi Barrera
 */
public class RequestLogCreatorFactory {

    /**
     * Creates a {@link RequestLogHandler} that uses the given request log class/instance.
     *
     * @param requestLogWrapper The wrapped request log to use to log requests.
     * @return The RequestLogHandler.
     */
    public static RequestLogHandler create(RequestLogWrapper requestLogWrapper) {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog((RequestLog)requestLogWrapper.getRequestLog());
        return requestLogHandler;
    }
}
