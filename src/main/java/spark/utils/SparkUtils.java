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
package spark.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Some utility methods
 *
 * @author Per Wendel
 */
public final class SparkUtils {

    public static final String ALL_PATHS = "+/*paths";
    
    private SparkUtils() {}
    
    public static List<String> convertRouteToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
            }
        }
        return path;
    }
    
    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }

    public static boolean isSplat(String routePart) {
        return routePart.equals("*");
    }

	/**
	 * Define this in a single place, so that we aren't subject to the system-dependent local character set.
	 * This also wraps the exception, which we can reasonably expect to never be thrown.
	 */
	public static byte[] stringToBytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return ("Spark: utf-8 not supported (string to bytes)").getBytes();
		}
	}

	/**
	 * This is the converse of stringToBytes.
	 */
	public static String bytesToString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "Spark: utf-8 not supported (bytes to string)";
		}
	}

}
