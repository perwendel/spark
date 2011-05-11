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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class WebContext {
    
    private static Logger LOG = Logger.getLogger(WebContext.class);
    
    private Map<String, String> params;
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    public WebContext(RouteMatch match, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        params = new HashMap<String, String>();
        setParams(match);
    }
    
    public String getParam(String param) {
        return params.get(param);
    }
    
    public void setContentType(String type) {
        response.setContentType(type);
    }
    
    
    public String getQueryParam(String queryParam) {
        return request.getParameter(queryParam);
    }
    
    public String getHeader(String header) {
        return request.getHeader(header);
    }
    
    @SuppressWarnings("unchecked")
    public Set<String> getQueryParams() {
        return request.getParameterMap().keySet();
    }
    
    public String getQueryString() {
        return request.getQueryString();
    }
    
    // TODO: implement error handling
    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void returnType() {
        
    }

    private final void setParams(RouteMatch match) {
        LOG.info("set params for requestUri: " + match.getRequestUri() + ", matchUri: " + match.getMatchUri());
    
        List<String> request = SparkUtils.convertRouteToList(match.getRequestUri());
        List<String> matched = SparkUtils.convertRouteToList(match.getMatchUri());
        
        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                LOG.info("matchedPart: " + SparkUtils.getParamName(matchedPart) + " = " + request.get(i));    
                params.put(SparkUtils.getParamName(matchedPart), request.get(i));
            }
        }
    }
    
}