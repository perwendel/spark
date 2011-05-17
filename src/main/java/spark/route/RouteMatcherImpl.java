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
package spark.route;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class for matching a request URI to a annotaion "configured" route.
 *
 * @author Per Wendel
 */
class RouteMatcherImpl implements RouteMatcher {
    
    private static Logger LOG = Logger.getLogger(RouteMatcherImpl.class);
    
    private static final String ROOT = "/";
    private static final char SINGLE_QUOTE = '\'';
    
    /* Http method root nodes */
    private Node get;
    private Node post;
    private Node put;
    private Node delete;
    private Node trace;
    private Node connect;
    private Node head;
    private Node options;

    /* Filter root node */
    private Node before;
    private Node after;
    
    /**
     * Constructor
     */
    public RouteMatcherImpl() {
        get = Node.createNode(HttpMethod.get.toString(), null, true);
        post = Node.createNode(HttpMethod.post.toString(), null, true);
        put = Node.createNode(HttpMethod.put.toString(), null, true);
        delete = Node.createNode(HttpMethod.delete.toString(), null, true);
        trace = Node.createNode(HttpMethod.trace.toString(), null, true);
        connect = Node.createNode(HttpMethod.connect.toString(), null, true);
        head = Node.createNode(HttpMethod.head.toString(), null, true);
        options = Node.createNode(HttpMethod.options.toString(), null, true);
        before = Node.createNode(HttpMethod.before.toString(), null, true);
        after = Node.createNode(HttpMethod.after.toString(), null, true);
    }
    
    @Override
    public void parseValidateAddRoute(String route, Object target) {
        try {
            int singleQuoteIndex = route.indexOf(SINGLE_QUOTE);
            String httpMethod = route.substring(0, singleQuoteIndex).trim().toLowerCase();
            String url = route.substring(singleQuoteIndex + 1, route.length() - 1).trim().toLowerCase();

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
    
    @Override
    public RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route) {
        Node rootNode = getRootNode(httpMethod);
        Node bestMatch = null;
        if (route.equals(ROOT)) {
            if (rootNode.getTarget() != null) {
                bestMatch = rootNode;
            }
        } else {
            bestMatch = rootNode.findBestMatch(route);
        }
        if (bestMatch == null) {
            // Root matches everything
            if (rootNode.getTarget() != null) {
                bestMatch = rootNode;
            }
        }
        if (bestMatch == null) {
            return null;    
        }
        return new RouteMatch(httpMethod, bestMatch.getTarget(), bestMatch.getPath(), route);
    }

    
    private void addRoute(HttpMethod httpMethod, String route, Object target) {
        LOG.info("Adding route: " + httpMethod + " '" + route + "'");
        Node rootNode = getRootNode(httpMethod);
        if (route.equals(ROOT)) {
            // Special root case
            rootNode.setTarget(target);
        }
        addBranch(rootNode, route, target);
    }

    private void addBranch(Node root, String route, Object target) {
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
        lastAdded.setTarget(target);
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
            case before:
                rootNode = before;
                break;
            case after:
                rootNode = after;
                break;
            default:
                rootNode = get;
                break;
        }
        return rootNode;
    }

}
