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

/**
 * Class for matching a request URI to a annotaion "configured" route.
 *
 * @author Per Wendel
 */
class RouteMatcher {

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

    /**
     * Constructor
     */
    public RouteMatcher() {
        get = Node.createNode(HttpMethod.get.toString(), null, true);
        post = Node.createNode(HttpMethod.post.toString(), null, true);
        put = Node.createNode(HttpMethod.put.toString(), null, true);
        delete = Node.createNode(HttpMethod.delete.toString(), null, true);
        trace = Node.createNode(HttpMethod.trace.toString(), null, true);
        connect = Node.createNode(HttpMethod.connect.toString(), null, true);
        head = Node.createNode(HttpMethod.head.toString(), null, true);
        options = Node.createNode(HttpMethod.options.toString(), null, true);
    }
    
    public void parseValidateAddRoute(String route, Method target) {
        try {
            System.out.println("Route: " + route);
            if (!SparkUtils.isStatic(target)) {
                LOG.error("The @Route value: " + route + " target method is not static. It MUST be static");
                return;
            }
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
    
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route) {
        Node rootNode = getRootNode(httpMethod);
        Node bestMatch = rootNode.findBestMatch(route);
        if (bestMatch == null) {
            return null;    
        }
        return new RouteMatch(bestMatch.getTarget(), bestMatch.getPath(), route);
    }

    
    private void addRoute(HttpMethod httpMethod, String route, Method target) {
        LOG.info("Adding route: " + httpMethod + " '" + route + "'");
        Node rootNode = getRootNode(httpMethod);
        addBranch(rootNode, route, target);
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

}
