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
package spark.http.matching;

import javax.servlet.http.HttpServletResponse;

import spark.ExceptionHandlerImpl;
import spark.ExceptionMapper;
import spark.UriNotFoundMapper;
import spark.Request;
import spark.RequestResponseFactory;
import spark.UriNotFoundHandler;

/**
 * Modifies the HTTP response and body based on the provided exception and request/response wrappers.
 */
final class GeneralError {

    private static final String INTERNAL_ERROR = "<html><body><h2>500 Internal Error</h2></body></html>";

    /**
     * Modifies the HTTP response and body based on the provided exception.
     */
    static void modify(HttpServletResponse httpResponse,
                       Body body,
                       RequestWrapper requestWrapper,
                       ResponseWrapper responseWrapper,
                       Exception e) {

        ExceptionHandlerImpl handler = ExceptionMapper.getInstance().getHandler(e);

        if (handler != null) {
            handler.handle(e, requestWrapper, responseWrapper);
            String bodyAfterFilter = responseWrapper.getDelegate().body();

            if (bodyAfterFilter != null) {
                body.set(bodyAfterFilter);
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            body.set(INTERNAL_ERROR);
        }
    }
    
    private static final String NOT_FOUND = "<html><body><h2>404 Not found</h2></body></html>";
    
     /**
     * Modifies the HTTP response and body based on the provided not found handler.
     */
    static void notFound(HttpServletResponse httpResponse, RouteContext context) {

        Body body = context.body();
        
        UriNotFoundHandler handler = UriNotFoundMapper.getInstance().getNotFoundHandler();
        
        if (handler != null) {
            if (context.requestWrapper().getDelegate() == null) {
              Request request = RequestResponseFactory.create(context.httpRequest());
              context.requestWrapper().setDelegate(request);
            }
            context.responseWrapper().setDelegate(context.response());
            
            handler.handle(context.requestWrapper(), context.responseWrapper());
            String bodyAfterFilter = context.responseWrapper().getDelegate().body();

            if (bodyAfterFilter != null) {
                body.set(bodyAfterFilter);
            }
            
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            body.set(NOT_FOUND);
        }
    }
    
}
