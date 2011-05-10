/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class RouteMatcher {

    private static Logger LOG = Logger.getLogger(RouteMatcher.class);
    
    /* Http method root nodes */
    private Node get;
    private Node post;
    private Node put;
    private Node delete;
    private Node trace;
    private Node connect;
    private Node head;
    private Node options;

    public RouteMatcher() {
        get = Node.createNode("get", null, true);
        post = Node.createNode("post", null, true);
        put = Node.createNode("put", null, true);
        delete = Node.createNode("delete", null, true);
        trace = Node.createNode("trace", null, true);
        connect = Node.createNode("connect", null, true);
        head = Node.createNode("head", null, true);
        options = Node.createNode("options", null, true);
    }
    
    public void parseValidateAddRoute(String route, Method target) {
        try {
            System.out.println("Route: " + route);
            int singleQuoteIndex = route.indexOf('\'');
            System.out.println(singleQuoteIndex);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase();
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim().toLowerCase();
            System.out.println(httpMethod + ", " + url);

            // Use special enum stuff to get from value
            HttpMethod method;
            try {
                method = HttpMethod.valueOf(httpMethod);    
            } catch (IllegalArgumentException e) {
                LOG.error("The @Route value: " + route + " has an invalid HTTP method part: " + httpMethod + ".");               
                return;    
            }
            addRoute(method, url, target);
        } catch (Exception e) {
            LOG.error("The @Route value: " + route + " is not in the correct format", e);
        }
    }

    
    private void addRoute(HttpMethod httpMethod, String route, Method target) {
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
            case head:
                rootNode = head;
                break;
            case options:
                rootNode = options;
                break;
            case trace:
                rootNode = trace;
                break;
            case connect:
                rootNode = connect;
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
