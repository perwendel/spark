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
package spark;

public abstract class ExceptionHandlerImpl<T extends Exception> implements ExceptionHandler<T> {

    /**
     * Holds the type of exception that this filter will handle
     */
    protected Class<? extends T> exceptionClass;

    /**
     * Initializes the filter with the provided exception type
     *
     * @param exceptionClass Type of exception
     */
    public ExceptionHandlerImpl(Class<T> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    /**
     * Returns type of exception that this filter will handle
     *
     * @return Type of exception
     */
    public Class<? extends T> exceptionClass() {
        return this.exceptionClass;
    }

    /**
     * Sets the type of exception that this filter will handle
     *
     * @param exceptionClass Type of exception
     */
    public void exceptionClass(Class<? extends T> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    /**
     * Invoked when an exception that is mapped to this handler occurs during routing
     *
     * @param exception The exception that was thrown during routing
     * @param request   The request object providing information about the HTTP request
     * @param response  The response object providing functionality for modifying the response
     */
    public abstract void handle(T exception, Request request, Response response);
}
