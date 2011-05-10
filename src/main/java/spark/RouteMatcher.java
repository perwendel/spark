package spark;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RouteMatcher {

    private static Logger LOG = Logger.getLogger(RouteMatcher.class);
    
    private Node get;
    private Node post;
    private Node put;
    private Node delete;

    public RouteMatcher() {
        get = Node.createNode("get", null, true);
        post = Node.createNode("post", null, true);
        put = Node.createNode("get", null, true);
        delete = Node.createNode("get", null, true);
    }

    public void addRoute(HttpMethod httpMethod, String route, Method target) {
        LOG.info("Adding route: " + httpMethod + " '" + route + "'");
        Node rootNode = getRootNode(httpMethod);
        addBranch(rootNode, route, target);
    }

    public RouteMatch findTargetForRoute(HttpMethod httpMethod, String route) {
        Node rootNode = getRootNode(httpMethod);
        Node bestMatch = rootNode.findBestMatch(route);
        if (bestMatch == null) {
            return null;    
        }
        return new RouteMatch(bestMatch.getTarget(), bestMatch.getPath(), route);
    }

    private Node getRootNode(HttpMethod httpMethod) {
        Node rootNode = null;
        switch (httpMethod) {
            case get:
                rootNode = get;
                break;
            case post:
                rootNode = post;
                break;
            case put:
                rootNode = put;
                break;
            case delete:
                rootNode = delete;
                break;
            default:
                rootNode = get;
                break;
        }
        return rootNode;
    }
    
    private static void addBranch(Node root, String route, Method method) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        Node lastAdded = root;
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
                Node child = Node.createNode(p, null);
                lastAdded.appendChild(child);
                lastAdded = child;
            }
        }
        lastAdded.setMethod(method);
    }

}
