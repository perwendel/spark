//
//  ========================================================================
//  Copyright (c) 1995-2015 Mort Bay Consulting Pty. Ltd.
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

import java.lang.reflect.Method;
import java.util.HashMap;

/* ------------------------------------------------------------ */

/**
 * TYPE Utilities.
 * Provides various static utiltiy methods for manipulating types and their
 * string representations.
 *
 * @since Jetty 4.1
 */
public class TypeUtil {

    /* ------------------------------------------------------------ */
    private static final HashMap<String, Class<?>> name2Class = new HashMap<>();

    static {
        name2Class.put("boolean", java.lang.Boolean.TYPE);
        name2Class.put("byte", java.lang.Byte.TYPE);
        name2Class.put("char", java.lang.Character.TYPE);
        name2Class.put("double", java.lang.Double.TYPE);
        name2Class.put("float", java.lang.Float.TYPE);
        name2Class.put("int", java.lang.Integer.TYPE);
        name2Class.put("long", java.lang.Long.TYPE);
        name2Class.put("short", java.lang.Short.TYPE);
        name2Class.put("void", java.lang.Void.TYPE);

        name2Class.put("java.lang.Boolean.TYPE", java.lang.Boolean.TYPE);
        name2Class.put("java.lang.Byte.TYPE", java.lang.Byte.TYPE);
        name2Class.put("java.lang.Character.TYPE", java.lang.Character.TYPE);
        name2Class.put("java.lang.Double.TYPE", java.lang.Double.TYPE);
        name2Class.put("java.lang.Float.TYPE", java.lang.Float.TYPE);
        name2Class.put("java.lang.Integer.TYPE", java.lang.Integer.TYPE);
        name2Class.put("java.lang.Long.TYPE", java.lang.Long.TYPE);
        name2Class.put("java.lang.Short.TYPE", java.lang.Short.TYPE);
        name2Class.put("java.lang.Void.TYPE", java.lang.Void.TYPE);

        name2Class.put("java.lang.Boolean", java.lang.Boolean.class);
        name2Class.put("java.lang.Byte", java.lang.Byte.class);
        name2Class.put("java.lang.Character", java.lang.Character.class);
        name2Class.put("java.lang.Double", java.lang.Double.class);
        name2Class.put("java.lang.Float", java.lang.Float.class);
        name2Class.put("java.lang.Integer", java.lang.Integer.class);
        name2Class.put("java.lang.Long", java.lang.Long.class);
        name2Class.put("java.lang.Short", java.lang.Short.class);

        name2Class.put("Boolean", java.lang.Boolean.class);
        name2Class.put("Byte", java.lang.Byte.class);
        name2Class.put("Character", java.lang.Character.class);
        name2Class.put("Double", java.lang.Double.class);
        name2Class.put("Float", java.lang.Float.class);
        name2Class.put("Integer", java.lang.Integer.class);
        name2Class.put("Long", java.lang.Long.class);
        name2Class.put("Short", java.lang.Short.class);

        name2Class.put(null, java.lang.Void.TYPE);
        name2Class.put("string", java.lang.String.class);
        name2Class.put("String", java.lang.String.class);
        name2Class.put("java.lang.String", java.lang.String.class);
    }

    /* ------------------------------------------------------------ */
    private static final HashMap<Class<?>, String> class2Name = new HashMap<>();

    static {
        class2Name.put(java.lang.Boolean.TYPE, "boolean");
        class2Name.put(java.lang.Byte.TYPE, "byte");
        class2Name.put(java.lang.Character.TYPE, "char");
        class2Name.put(java.lang.Double.TYPE, "double");
        class2Name.put(java.lang.Float.TYPE, "float");
        class2Name.put(java.lang.Integer.TYPE, "int");
        class2Name.put(java.lang.Long.TYPE, "long");
        class2Name.put(java.lang.Short.TYPE, "short");
        class2Name.put(java.lang.Void.TYPE, "void");

        class2Name.put(java.lang.Boolean.class, "java.lang.Boolean");
        class2Name.put(java.lang.Byte.class, "java.lang.Byte");
        class2Name.put(java.lang.Character.class, "java.lang.Character");
        class2Name.put(java.lang.Double.class, "java.lang.Double");
        class2Name.put(java.lang.Float.class, "java.lang.Float");
        class2Name.put(java.lang.Integer.class, "java.lang.Integer");
        class2Name.put(java.lang.Long.class, "java.lang.Long");
        class2Name.put(java.lang.Short.class, "java.lang.Short");

        class2Name.put(null, "void");
        class2Name.put(java.lang.String.class, "java.lang.String");
    }

    /* ------------------------------------------------------------ */
    private static final HashMap<Class<?>, Method> class2Value = new HashMap<>();

    static {
        try {
            Class<?>[] s = {java.lang.String.class};

            class2Value.put(java.lang.Boolean.TYPE,
                            java.lang.Boolean.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Byte.TYPE,
                            java.lang.Byte.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Double.TYPE,
                            java.lang.Double.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Float.TYPE,
                            java.lang.Float.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Integer.TYPE,
                            java.lang.Integer.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Long.TYPE,
                            java.lang.Long.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Short.TYPE,
                            java.lang.Short.class.getMethod("valueOf", s));

            class2Value.put(java.lang.Boolean.class,
                            java.lang.Boolean.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Byte.class,
                            java.lang.Byte.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Double.class,
                            java.lang.Double.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Float.class,
                            java.lang.Float.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Integer.class,
                            java.lang.Integer.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Long.class,
                            java.lang.Long.class.getMethod("valueOf", s));
            class2Value.put(java.lang.Short.class,
                            java.lang.Short.class.getMethod("valueOf", s));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Parse an int from a substring.
     * Negative numbers are not handled.
     *
     * @param s      String
     * @param offset Offset within string
     * @param length Length of integer or -1 for remainder of string
     * @param base   base of the integer
     * @return the parsed integer
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static int parseInt(String s, int offset, int length, int base)
        throws NumberFormatException {
        int value = 0;

        if (length < 0) {
            length = s.length() - offset;
        }

        for (int i = 0; i < length; i++) {
            char c = s.charAt(offset + i);

            int digit = convertHexDigit((int) c);
            if (digit < 0 || digit >= base) {
                throw new NumberFormatException(s.substring(offset, offset + length));
            }
            value = value * base + digit;
        }
        return value;
    }

    /* ------------------------------------------------------------ */
    public static String toString(byte[] bytes, int base) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            int bi = 0xff & b;
            int c = '0' + (bi / base) % base;
            if (c > '9') {
                c = 'a' + (c - '0' - 10);
            }
            buf.append((char) c);
            c = '0' + bi % base;
            if (c > '9') {
                c = 'a' + (c - '0' - 10);
            }
            buf.append((char) c);
        }
        return buf.toString();
    }

    /* ------------------------------------------------------------ */

    /**
     * @param c An ASCII encoded character 0-9 a-f A-F
     * @return The byte value of the character 0-16.
     */
    public static int convertHexDigit(char c) {
        int d = ((c & 0x1f) + ((c >> 6) * 0x19) - 0x10);
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }

    /* ------------------------------------------------------------ */

    /**
     * @param c An ASCII encoded character 0-9 a-f A-F
     * @return The byte value of the character 0-16.
     */
    public static int convertHexDigit(int c) {
        int d = ((c & 0x1f) + ((c >> 6) * 0x19) - 0x10);
        if (d < 0 || d > 15) {
            throw new NumberFormatException("!hex " + c);
        }
        return d;
    }

    /* ------------------------------------------------------------ */
    public static String toHexString(byte b) {
        return toHexString(new byte[] {b}, 0, 1);
    }

    /* ------------------------------------------------------------ */
    public static String toHexString(byte[] b, int offset, int length) {
        StringBuilder buf = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            int bi = 0xff & b[i];
            int c = '0' + (bi / 16) % 16;
            if (c > '9') {
                c = 'A' + (c - '0' - 10);
            }
            buf.append((char) c);
            c = '0' + bi % 16;
            if (c > '9') {
                c = 'a' + (c - '0' - 10);
            }
            buf.append((char) c);
        }
        return buf.toString();
    }
}
