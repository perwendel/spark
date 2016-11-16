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
package spark.embeddedserver;

/**
 * Used to indicate that a feature is not supported for the specific embedded server.
 */
public class NotSupportedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Raises a NotSupportedException for the provided class name and feature name.
     *
     * @param clazz   the class name
     * @param feature the feature name
     */
    public static void raise(String clazz, String feature) {
        throw new NotSupportedException(clazz, feature);
    }

    private NotSupportedException(String clazz, String feature) {
        super("'" + clazz + "' doesn't support '" + feature + "'");

    }

}
