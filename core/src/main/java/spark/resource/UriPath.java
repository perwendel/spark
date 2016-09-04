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

/**
 * Used for converting URL paths.
 */
public class UriPath {

    /**
     * Convert a path to a cananonical form.
     * All instances of "." and ".." are factored out.  Null is returned
     * if the path tries to .. above its root.
     *
     * @param path the path to convert
     * @return path or null.
     */
    public static String canonical(String path) {
        if (path == null || path.length() == 0) {
            return path;
        }

        int end = path.length();
        int start = path.lastIndexOf('/', end);

        search:
        while (end > 0) {
            switch (end - start) {
                case 2: // possible single dot
                    if (path.charAt(start + 1) != '.') {
                        break;
                    }
                    break search;
                case 3: // possible double dot
                    if (path.charAt(start + 1) != '.' || path.charAt(start + 2) != '.') {
                        break;
                    }
                    break search;
            }

            end = start;
            start = path.lastIndexOf('/', end - 1);
        }

        // If we have checked the entire string
        if (start >= end) {
            return path;
        }

        StringBuilder buf = new StringBuilder(path);
        int delStart = -1;
        int delEnd = -1;
        int skip = 0;

        while (end > 0) {
            switch (end - start) {
                case 2: // possible single dot
                    if (buf.charAt(start + 1) != '.') {
                        if (skip > 0 && --skip == 0) {
                            delStart = start >= 0 ? start : 0;
                            if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                                delStart++;
                            }
                        }
                        break;
                    }

                    if (start < 0 && buf.length() > 2 && buf.charAt(1) == '/' && buf.charAt(2) == '/') {
                        break;
                    }

                    if (delEnd < 0) {
                        delEnd = end;
                    }
                    delStart = start;
                    if (delStart < 0 || delStart == 0 && buf.charAt(delStart) == '/') {
                        delStart++;
                        if (delEnd < buf.length() && buf.charAt(delEnd) == '/') {
                            delEnd++;
                        }
                        break;
                    }
                    if (end == buf.length()) {
                        delStart++;
                    }

                    end = start--;
                    while (start >= 0 && buf.charAt(start) != '/') {
                        start--;
                    }
                    continue;

                case 3: // possible double dot
                    if (buf.charAt(start + 1) != '.' || buf.charAt(start + 2) != '.') {
                        if (skip > 0 && --skip == 0) {
                            delStart = start >= 0 ? start : 0;
                            if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                                delStart++;
                            }
                        }
                        break;
                    }

                    delStart = start;
                    if (delEnd < 0) {
                        delEnd = end;
                    }

                    skip++;
                    end = start--;
                    while (start >= 0 && buf.charAt(start) != '/') {
                        start--;
                    }
                    continue;

                default:
                    if (skip > 0 && --skip == 0) {
                        delStart = start >= 0 ? start : 0;
                        if (delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                            delStart++;
                        }
                    }
            }

            // Do the delete
            if (skip <= 0 && delStart >= 0 && delEnd >= delStart) {
                buf.delete(delStart, delEnd);
                delStart = delEnd = -1;
                if (skip > 0) {
                    delEnd = end;
                }
            }

            end = start--;
            while (start >= 0 && buf.charAt(start) != '/') {
                start--;
            }
        }

        // Too many ..
        if (skip > 0) {
            return null;
        }

        // Do the delete
        if (delEnd >= 0) {
            buf.delete(delStart, delEnd);
        }

        return buf.toString();
    }

}
