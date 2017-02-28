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
package spark.resource;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

/**
 * Used for converting URL paths.
 */
public class UriPath {

    private static final Pattern __PATH_SPLIT = Pattern.compile("(?<=\\/)");

    /**
     * Convert a path to a canonical form.
     * All instances of "." and ".." are factored out.  Null is returned
     * if the path tries to .. above its root.
     *
     * @param path the path to convert
     * @return path or null.
     */
    public static String canonical(String path) {
        if (path == null || path.isEmpty() || !path.contains(".")) {
            return path;
        }

        if (path.startsWith("/..")) {
            return null;
        }

        List<String> directories = new LinkedList<>();
        Collections.addAll(directories, __PATH_SPLIT.split(path));

        for (ListIterator<String> iterator = directories.listIterator(); iterator.hasNext(); ) {
            switch (iterator.next()) {
                case "./":
                case ".":
                    if (iterator.hasNext() && directories.get(iterator.nextIndex()).equals("/")) {
                        break;
                    }

                    iterator.remove();
                    break;
                case "../":
                case "..":
                    if (iterator.previousIndex() == 0) {
                        return null;
                    }

                    iterator.remove();
                    if (iterator.previous().equals("/") && iterator.nextIndex() == 0) {
                        return null;
                    }

                    iterator.remove();
                    break;
            }
        }

        return String.join("", directories);
    }
}