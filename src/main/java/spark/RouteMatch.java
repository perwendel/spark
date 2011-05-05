package spark;

import java.lang.reflect.Method;

public class RouteMatch {

    private Method target;
    private String matchUri;
    private String requestUri;
    
    public RouteMatch(Method target, String matchUri, String requestUri) {
        super();
        this.target = target;
        this.matchUri = matchUri;
        this.requestUri = requestUri;
    }

    
    /**
     * @return the target
     */
    public Method getTarget() {
        return target;
    }

    
    /**
     * @return the matchUri
     */
    public String getMatchUri() {
        return matchUri;
    }

    
    /**
     * @return the requestUri
     */
    public String getRequestUri() {
        return requestUri;
    }
    
    
    
}
