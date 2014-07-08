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
import javax.servlet.MultipartConfigElement;
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

    private static final String LOG_FMT = "%d %s %s (%s) %.2fms";

    private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
            System.getProperty("java.io.tmpdir"));

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
        try {
            long start, cost;

            String contentType = request.getContentType();
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
            }

            start = System.nanoTime();
            filter.doFilter(request, response, null);
            baseRequest.setHandled(true);
            cost = System.nanoTime() - start;

            LOG.info(String.format(LOG_FMT, response.getStatus(),
                    request.getMethod().toUpperCase(), getRequestLine(request),
                    request.getRemoteHost(), cost / 1000000.0));

        } catch (NotConsumedException ignore) {
            // TODO : Not use an exception in order to be faster.
            baseRequest.setHandled(false);
        }
    }

    private String getRequestLine(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isEmpty()) {
            return request.getRequestURI();
        }
        return String.format("%s?%s", request.getRequestURI(), query);
    }

}
