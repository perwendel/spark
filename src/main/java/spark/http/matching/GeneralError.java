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

import spark.ExceptionHandlerImpl;
import spark.ExceptionMapper;
import spark.utils.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

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
            e.printStackTrace();
            InputStream inputStream = GeneralError.class.getClassLoader().getResourceAsStream("500.html");
            String contents = ResourceUtils.readResourceContents(inputStream);
            if(contents != null) body.set(contents);
            else body.set(INTERNAL_ERROR);
        }
    }

}
