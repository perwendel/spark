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
package spark.webserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Asynchronous version of the JettyHandler
 *
 * @author Gale Dunkleberger
 */
class AsyncJettyHandler extends JettyHandler {
    private static final Logger LOG = Log.getLogger(AsyncJettyHandler.class);
    private ExecutorService executorService;

    public AsyncJettyHandler(Filter filter, ExecutorService executorService) {
        super(filter);
        this.filter = filter;
        this.executorService = executorService;
    }

    @Override
    public void doHandle(String target, final Request baseRequest,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        LOG.debug("jettyhandler, handle();");
        Boolean processed = (Boolean) request.getAttribute("processed");
        if (processed == null) {
            final Continuation continuation = ContinuationSupport
                    .getContinuation(request);
            if (continuation.isExpired()) {
                baseRequest.setHandled(false);
                return;
            }
            continuation.setTimeout(30000);
            continuation.suspend();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        filter.doFilter(request, response, null);
                        baseRequest.setHandled(true);
                    } catch (NotConsumedException ignore) {
                        baseRequest.setHandled(false);
                    } catch (IOException e) {
                        baseRequest.setHandled(false);
                    } catch (ServletException e) {
                        baseRequest.setHandled(false);
                    } finally {
                        synchronized (request) {
                            request.setAttribute("processed", Boolean.TRUE);
                            continuation.complete();
                        }
                    }
                }
            });
        }
    }
}