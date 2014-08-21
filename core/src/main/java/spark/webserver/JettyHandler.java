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

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Simple Jetty Handler
 *
 * @author Per Wendel
 */
class JettyHandler extends SessionHandler {

    private static final Logger LOG = Log.getLogger(JettyHandler.class);

    private Filter filter;

    public JettyHandler(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void doHandle(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        LOG.debug("jettyhandler, handle();");
        try {
            filter.doFilter(request, response, null);
            baseRequest.setHandled(true);
        } catch (NotConsumedException ignore) {
            // TODO : Not use an exception in order to be faster.
            baseRequest.setHandled(false);
        }
    }

}