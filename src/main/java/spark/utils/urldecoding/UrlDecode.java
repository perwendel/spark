//
//  ========================================================================
//  Copyright (c) 1995-2017 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package spark.utils.urldecoding;

import com.sun.istack.internal.NotNull;

public class UrlDecode {


    /** .Decode a URI path and strip parameters
     * @param path the {@code String} url path of a request website
     * @return {@code String} if all elements of parameter path is utf8 form,
     * if some are not utf8 form (catch the NotUtf8Exception),
     * return the return value of method  decodeISO88591Path
     */
    public static String path(String path) {
        return path(path, 0, path.length());
    }

    /*------------------------------------------------------------ */
    /**
     * CS304 Issue link: https://github.com/perwendel/spark/issues/1030
     * modify 112, 67-70
     * .Decode a URI path and strip parameters
     * @param path the {@code String} url path of a request website
     * @param offset the {@code int} the start position of input String path.
     * @param length the {@code int} the length parameter path is used in this method.
     * this method will only analyze path from offset to offset + length.
     * @return {@code String} if all elements of parameter path is utf8 form,
     * if some are not utf8 form (catch the NotUtf8Exception)
     * return the return value of method  decodeISO88591Path
     * which directly change the non-utf8 part to int
    */
    public static String path(String path, int offset, int length) {
        try {
            Utf8StringBuilder builder = null;
            int end = offset + length;
            for (int i = offset; i < end; i++) {
                char c = path.charAt(i);
                switch (c) {
                    case '%':
                        if (builder == null) {
                            builder = new Utf8StringBuilder(path.length());
                            builder.append(path, offset, i - offset);
                        }
                        if ((i + 2) < end) {
                            char u = path.charAt(i + 1);
                            if (u == 'u') {
                                // TODO this is wrong. This is a codepoint not a char
                                //TODO need to reduce this check process
                                CodePoint cp = new CodePoint();
                                int movenumber = cp.makeCodepointFromString(path,i,end);
                                builder.append(cp.getCodepoint(),0,cp.length());
                                i += movenumber;
                            } else {
                                builder.append((byte) (0xff & (TypeUtil.convertHexDigit(u) * 16
                                    + TypeUtil.convertHexDigit(path.charAt(i + 2)))));
                                i += 2;
                            }
                        } else {
                            throw new IllegalArgumentException("Bad URI % encoding");
                        }

                        break;

                    case ';':
                        if (builder == null) {
                            builder = new Utf8StringBuilder(path.length());
                            builder.append(path, offset, i - offset);
                        }

                        while (++i < end) {
                            if (path.charAt(i) == '/') {
                                builder.append('/');
                                break;
                            }
                        }

                        break;

                    default:
                        if (builder != null) {
                            builder.append(c);
                        }
                        break;
                }
            }

            if (builder != null) {
                return builder.toString();
            }
            if (offset == 0 && length == path.length()) {
                return path;
            }
            return path.substring(offset, end);
        } catch (Utf8Appendable.NotUtf8Exception e) {
            return decodeISO88591Path(path, offset, length);
        }
    }

    public static class CodePoint {
        private String codepoint;

        /**.
         * get the codepoint value as a String
         * @return {@code String} that contain a codepoint
         */
        public String getCodepoint() {
            return codepoint;
        }

        /**.
         * get the length of codepoint on base of char
         * @return the number of char in this codepoint
         */
        public int length() {
            return codepoint.length();
        }

        /**
         *
         * conbine the char to the codepoint and return how many char should be skip
         * TODO: because this function is used in the UrlDecoder.path,
         *      the first check about char a is not need.
         *      so if want to use in other function, add the check about char a first.
         * @param path {@code String} which contain the codepoint in form %uXXXX
         * @param offset {@code int} which show the start position of the codepoint
         * @param length {@code int} the length of the String
         * @return {@code int} the number that the String should skip in UrlDecode.path()
         *         11 if the length of codepint is 2 char
         *         5 if the length is 1 char
         */
        public int makeCodepointFromString(@NotNull String path, int offset, int length) {
            char firstchar = (char) (0xffff & TypeUtil.parseInt(path, offset + 2, 4, 16));
            if ((offset + 11) < length) {
                if (path.charAt(offset + 6) == '%' && path.charAt(offset + 7) == 'u') {
                    int nextoffset = offset + 6;
                    char secondchar = (char) (0xffff & TypeUtil.parseInt(path, nextoffset + 2, 4, 16));
                    codepoint = String.valueOf(firstchar) + secondchar;
                    if (Character.isHighSurrogate(firstchar) &&
                        Character.isLowSurrogate(secondchar)) {
                        return 11;
                    }
                }
            }
            codepoint = String.valueOf(firstchar);
            return 5;
        }
    }

    /* ------------------------------------------------------------ */
    /* .Decode a URI path and strip parameters of ISO-8859-1 path
     */
    private static String decodeISO88591Path(String path, int offset, int length) {
        StringBuilder builder = null;
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            char c = path.charAt(i);
            switch (c) {
                case '%':
                    if (builder == null) {
                        builder = new StringBuilder(path.length());
                        builder.append(path, offset, i - offset);
                    }
                    if ((i + 2) < end) {
                        char u = path.charAt(i + 1);
                        if (u == 'u') {
                            // TODO this is wrong. This is a codepoint not a char
                            builder.append((char) (0xffff & TypeUtil.parseInt(path, i + 2, 4, 16)));
                            i += 5;
                        } else {
                            builder.append((byte) (0xff & (TypeUtil.convertHexDigit(u) * 16
                                + TypeUtil.convertHexDigit(path.charAt(i + 2)))));
                            i += 2;
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }

                    break;

                case ';':
                    if (builder == null) {
                        builder = new StringBuilder(path.length());
                        builder.append(path, offset, i - offset);
                    }
                    while (++i < end) {
                        if (path.charAt(i) == '/') {
                            builder.append('/');
                            break;
                        }
                    }
                    break;

                default:
                    if (builder != null) {
                        builder.append(c);
                    }
                    break;
            }
        }

        if (builder != null) {
            return builder.toString();
        }
        if (offset == 0 && length == path.length()) {
            return path;
        }
        return path.substring(offset, end);
    }

}
