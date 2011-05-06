package spark;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class WebContext {
    
    private static Logger LOG = Logger.getLogger(WebContext.class);
    
    private Map<String, String> params;
    
    public WebContext(RouteMatch match) {
        params = new HashMap<String, String>();
        setParams(match);
    }
    
    private final void setParams(RouteMatch match) {
        LOG.info("set params for requestUri: " + match.getRequestUri() + ", matchUri: " + match.getMatchUri());
    
        // TODO: Implement this
    }
    
    public String getParam(String paramName) {
        return params.get(paramName);
    }
    
    public void returnType() {
        
    }
    
}