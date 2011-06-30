/*
 * Copyright 2011- Per Wendel, Matthias Hryniszak
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
package spark.route.internal;

import java.util.List;

import spark.utils.SparkUtils;

public class PathMatcher {
	public boolean match(String template, String path) {
		List<String> templateItems = SparkUtils.convertRouteToList(template);
		List<String> pathItems = SparkUtils.convertRouteToList(path);
		int count = Math.min(templateItems.size(), pathItems.size());
		for (int i = 0; i < count; i++) {
			if (templateItems.get(i).equals("*")) 
				return true;
			if (templateItems.get(i).startsWith(":")) 
				continue;
			String purePath = extractPurePath(pathItems.get(i));
			if (!templateItems.get(i).equals(purePath))
				return false;
		}
		return templateItems.size() == pathItems.size();
	}
	
	private String extractPurePath(String path) {
		int delimiterIndex = path.indexOf('?');
		if (delimiterIndex == -1) return path;
		return path.substring(0, delimiterIndex - 1);
	}
}
