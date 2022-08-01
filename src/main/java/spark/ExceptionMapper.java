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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExceptionMapper {

    /**
     * Holds an exception mapper instance for use in servlet mode
     */
    private static ExceptionMapper servletInstance;

    @Deprecated
    public static ExceptionMapper getInstance() {
        return getServletInstance();
    }

    /**
     * Returns exception mapper instance used in servlet mode
     *
     * @return servlet instance
     */
    public synchronized static ExceptionMapper getServletInstance() {
        if (servletInstance == null) {
            servletInstance = new ExceptionMapper();
        }
        return servletInstance;
    }

    /**
     * Holds a map of Exception classes and associated handlers
     */
    private Map<Class<? extends Exception>, ExceptionHandlerImpl> exceptionMap;

    /**
     * Class constructor
     */
    public ExceptionMapper() {
        this.exceptionMap = new ConcurrentHashMap<>();
    }

    /**
     * Maps the given handler to the provided exception type. If a handler was already registered to the same type, the
     * handler is overwritten.
     *
     * @param exceptionClass Type of exception
     * @param handler        Handler to map to exception
     */
    public void map(Class<? extends Exception> exceptionClass, ExceptionHandlerImpl handler) {
        this.exceptionMap.put(exceptionClass, handler);
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exceptionClass Type of exception
     * @return Associated handler
     */
    public ExceptionHandlerImpl getHandler(Class<? extends Exception> exceptionClass) {
        // If the exception map does not contain the provided exception class, it might
        // still be that a superclass of the exception class is.
        if (!this.exceptionMap.containsKey(exceptionClass)) {

            Class<?> superclass = exceptionClass.getSuperclass();
            do {
                // Is the superclass mapped?
                if (this.exceptionMap.containsKey(superclass)) {
                    // Use the handler for the mapped superclass, and cache handler
                    // for this exception class
                    ExceptionHandlerImpl handler = this.exceptionMap.get(superclass);
                    this.exceptionMap.put(exceptionClass, handler);
                    return handler;
                }

                // Iteratively walk through the exception class's superclasses
                superclass = superclass.getSuperclass();
            } while (superclass != null);

            // No handler found either for the superclasses of the exception class
            // We cache the null value to prevent future
            // We do not need to cache the null value. comment by lloyd.
            // this.exceptionMap.put(exceptionClass, null);
            return null;
        }

        // Direct map
        return this.exceptionMap.get(exceptionClass);
    }

    /**
     * Returns the handler associated with the provided exception class
     *
     * @param exception Exception that occurred
     * @return Associated handler
     */
    public ExceptionHandlerImpl getHandler(Exception exception) {
        return this.getHandler(exception.getClass());
    }

    /**
     * Clear the exception mappings.
     */
    public void clear() {
        this.exceptionMap.clear();
    }

}
