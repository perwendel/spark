package spark.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.FilterImpl;
import spark.RouteImpl;
import spark.utils.MimeParse;
import spark.utils.SparkUtils;
import spark.utils.StringUtils;

public class TrieRouteMatcher extends RouteMatcher{
	
	@Override
	public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod,
			String path, String acceptType) {
		List<RouteEntry> routeEntries = findRoutes(root, StringUtils.trim(path, '/').split("/"), 0, httpMethod);
		RouteEntry entry = findTargetWithGivenAcceptType(routeEntries, acceptType);
        return entry != null ? new RouteMatch(httpMethod, entry.target, entry.path, path, acceptType) : null;
	}

	@Override
	public List<RouteMatch> findTargetsForRequestedRoute(HttpMethod httpMethod,
			String path, String acceptType) {
		List<RouteMatch> matchSet = new ArrayList<>();
		List<RouteEntry> routeEntries = new ArrayList<>();
        findFilters(root, StringUtils.trim(path, '/').split("/"), 0, httpMethod, routeEntries);

        for (RouteEntry routeEntry : routeEntries) {
            if (acceptType != null) {
                String bestMatch = MimeParse.bestMatch(Arrays.asList(routeEntry.acceptedType), acceptType);

                if (routeWithGivenAcceptType(bestMatch)) {
                    matchSet.add(new RouteMatch(httpMethod, routeEntry.target, routeEntry.path, path, acceptType));
                }
            } else {
                matchSet.add(new RouteMatch(httpMethod, routeEntry.target, routeEntry.path, path, acceptType));
            }
        }

        return matchSet;
	}

	@Override
	public void clearRoutes() {
		this.root = new Node();
	}
	
	//-------------- Trie stuff!
	
	public TrieRouteMatcher() {
		this.root = new Node();
	}
	
	private Node root;

	class Node {
		Map<HttpMethod, List<RouteEntry>> filters;
		Map<HttpMethod, List<RouteEntry>> routes;
		Map<String, Node> children;

		public Node() {
			filters = new HashMap<>();
			routes = new HashMap<>();
			children = new HashMap<>();
		}
	}

	synchronized void addRoute(HttpMethod method, String route, String acceptType, Object target) {
		RouteEntry entry = new RouteEntry();
        entry.httpMethod = method;
        entry.path = route;
        entry.target = target;
        entry.acceptedType = acceptType;
        if ((method == HttpMethod.before || method == HttpMethod.after)
                && route.equals(SparkUtils.ALL_PATHS)) {
        	addRoute(root, new String[]{}, 0, entry);
        } else {
		addRoute(root, StringUtils.trim(route, '/').split("/"), 0, entry);
        }
	}

	private void addRoute(Node node, String[] route, int pos, RouteEntry entry) {
		if (pos == route.length) {
			if (entry.target instanceof RouteImpl) {
				List<RouteEntry> list = node.routes.get(entry.httpMethod);
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(entry);
				node.routes.put(entry.httpMethod, list);
			} else if (entry.target instanceof FilterImpl) {
				List<RouteEntry> list = node.filters.get(entry.httpMethod);
				if (list == null) {
					list = new ArrayList<>();
				}
				list.add(entry);
				node.filters.put(entry.httpMethod, list);
			}
			return;
		}

		String word = route[pos];
		if (word.startsWith(":")) {
			word = "*";
		}
		if (!node.children.containsKey(word)) {
			node.children.put(word, new Node());
		}
		addRoute(node.children.get(word), route, pos + 1, entry);
	}
	
	private List<RouteEntry> findRoutes(Node node, String[] route, int pos, HttpMethod method) {
		if (node == null) {
			return new ArrayList<>();
		}
		if (route.length == pos) {
			List<RouteEntry> entries = node.routes.get(method);
			if (entries == null) {
				entries = new ArrayList<>();
			}
			return entries;
		}
		
		String word = route[pos];
		if (!node.children.containsKey(word)) {
			word = "*";
		}
		return findRoutes(node.children.get(word), route, pos+1, method);
	}
	
	private void findFilters(Node node, String[] route, int pos, HttpMethod method, List<RouteEntry> filters) {
		if (node == null) {
			return;
		}
		List<RouteEntry> entries = node.filters.get(method);
		if (entries != null) {
			filters.addAll(entries);
		}
		if (route.length == pos) {
			return;
		}
		
		String word = route[pos];
		if (!node.children.containsKey(word)) {
			word = "*";
		}
		findFilters(node.children.get(word), route, pos+1, method, filters);
	}
	
}
