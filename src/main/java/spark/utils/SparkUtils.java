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

import java.util.ArrayList;
import java.util.List;

/**
 * Some utility methods
 * 
 * @author Per Wendel
 */
public final class SparkUtils {

	public static final String ALL_PATHS = "+/*paths";
	public static final String FORCE_PARAM_ESCAPE_REGEX = "/:\\*";
	public static final String FORCE_PARAM_ESCAPE = "/:*";

	private SparkUtils() {
	}

	public static List<String> convertRouteToList(String route) {
		String[] forceParam = route.split(FORCE_PARAM_ESCAPE_REGEX, 2);
		if (forceParam.length > 1) {
			route = forceParam[0];
		}

		String[] pathArray = route.split("/");
		List<String> path = new ArrayList<String>();
		for (String p : pathArray) {
			if (p.length() > 0) {
				path.add(p);
			}
		}

		if (forceParam.length > 1) {
			path.add(":" + forceParam[1]);
		}

		return path;
	}
	
	public static boolean isForceParam(String route){
		return route.contains(FORCE_PARAM_ESCAPE);
	}

	public static List<String> convertRouteToList(String route, int patternSize) {
		String[] pathArray;
		if (patternSize < 2) {
			if(route.startsWith("/")){
				route = route.substring(1);
			}
			pathArray = new String[] { route };
		} else {
			pathArray = route.split("/", patternSize + 1);
		}

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

}
