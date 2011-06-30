package spark.route.internal;

import java.util.List;

import spark.utils.SparkUtils;

public class PathMatcher {
	public boolean match(String template, String path2) {
		if (!template.endsWith("*") 
				&& ((path2.endsWith("/") && !template.endsWith("/")) || (template.endsWith("/") && !path2.endsWith("/")))) {
			// One and not both ends with slash
			return false;
		}
		if (template.equals(path2)) {
			// path2s are the same
			return true;
		}

		// check params
		List<String> thisPath2List = SparkUtils.convertRouteToList(template);
		List<String> path2List = SparkUtils.convertRouteToList(path2);

		int thispath2Size = thisPath2List.size();
		int path2Size = path2List.size();

		if (thispath2Size == path2Size) {
			for (int i = 0; i < thispath2Size; i++) {
				String thispath2Part = thisPath2List.get(i);
				String path2Part = path2List.get(i);

				if ((i == thispath2Size - 1)) {
					if (thispath2Part.equals("*") && template.endsWith("*")) {
						// wildcard match
						return true;
					}
				}

				if (!thispath2Part.startsWith(":")
						&& !thispath2Part.equals(path2Part)) {
					return false;
				}
			}
			// All parts matched
			return true;
		} else {
			// Number of "path2 parts" not the same
			// check wild card:
			if (template.endsWith("*")) {
				if (path2Size == (thispath2Size - 1) && (path2.endsWith("/"))) {
					// Hack for making wildcards work with trailing slash
					path2List.add("");
					path2List.add("");
					path2Size += 2;
				}

				if (thispath2Size < path2Size) {
					for (int i = 0; i < thispath2Size; i++) {
						String thispath2Part = thisPath2List.get(i);
						String path2Part = path2List.get(i);
						if (thispath2Part.equals("*")
								&& (i == thispath2Size - 1)
								&& template.endsWith("*")) {
							// wildcard match
							return true;
						}
						if (!thispath2Part.startsWith(":")
								&& !thispath2Part.equals(path2Part)) {
							return false;
						}
					}
					// All parts matched
					return true;
				}
				// End check wild card
			}
			return false;
		}
	}
}
