/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark.route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import spark.utils.SparkUtils;

class Node {
    private List<Node> children;
    private Method method;
    private String paramName;
    private boolean isRoot;
    
    public static Node createNode(String value, Method method, boolean isRoot) {
        Node node = new Node();
        node.setMethod(method);
        if (value.startsWith(":")) {
            node.setValue(":");
            node.setParamName(value.substring(1));
        } else {
            node.setValue(value);   
        }
        node.isRoot = isRoot;
        return node;
    }
    
    public static Node createNode(String value, Method method) {
        return createNode(value, method, false);
    }
    
    public Method getTarget() {
        return method;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    private String value;
    private Node parent;
    
    public Node() {
        children = new ArrayList<Node>();
    }

    public List<Node> getChildren() {
        return children;
    }

    public void appendChild(Node node) {
        node.setParent(this);
        children.add(node);
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }
    
    public Node getParent() {
        return this.parent;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setParamName(String extra) {
        this.paramName = extra;
    }

    public String getParamName() {
        return paramName;
    }

    public boolean isParam() {
        return paramName != null;
    }

    public String getPath() {
        String buffer = encodePathPart();
        Node p = getParent();
        while (p != null && !p.isRoot) {
            buffer = p.encodePathPart() + "/" + buffer;
            p = p.getParent();
        }
        return buffer;
    }
    
    private String encodePathPart() {
        String val = getValue();
        if (val.equals(":")) {
            System.out.println("hlo");
            val += getParamName();
        }
        return val;
    }
    
    public Node findBestMatch(String route) {
        List<String> path = SparkUtils.convertRouteToList(route);
        SearchContext searchContext = new SearchContext();
        searchRec(path, 0, 0, searchContext);
        return searchContext.bestMatch;
    }
    
    private void searchRec(List<String> path, int currentDepth, int nbrParams, SearchContext info) {
        if (currentDepth >= path.size()) {
            return;
        }
        String search = path.get(currentDepth).trim();
        List<Node> children = this.getChildren();

        for (Node childNode : children) {
            int currentNbrParams = nbrParams;
            if (childNode.getValue().equals(search) || childNode.isParam()) {
                if (childNode.isParam()) {
                    currentNbrParams++;
                }
                if (childNode.hasChildren()) {
                    // No "end" point
                    childNode.searchRec(path, currentDepth + 1, currentNbrParams, info);
                } else {
                    // End point                      
                    if (currentDepth > info.currentDeepestMatch) {
                        // This is a better match
                        info.setBestMarch(childNode, currentDepth, currentNbrParams);
                    } else if (currentDepth == info.currentDeepestMatch) {
                        if (currentNbrParams < info.bestMatchNbrParams) {
                            // A "better" match due to less wildcards in path:
                            info.setBestMarch(childNode, currentDepth, currentNbrParams);
                        }
                    }
                }
            } 
        }
    }
    
    private static class SearchContext {
        private void setBestMarch(Node bestMatch, int depth, int nbrParams) {
            this.bestMatch = bestMatch;
            this.currentDeepestMatch = depth;
            this.bestMatchNbrParams = nbrParams;
        }
        public int currentDeepestMatch = -1;
        public int bestMatchNbrParams = Integer.MAX_VALUE;
        public Node bestMatch = null;
    }
    
}