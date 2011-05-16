package spark;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.route.RouteMatch;
import spark.utils.SparkUtils;

public abstract class Route {

    private static Logger LOG = Logger.getLogger(Route.class);

    private Map<String, String> params;
    private HttpServletResponse response;

    protected Request request;

    private String route;
    
    protected Route(String route) {
        this.route = route;
    }
    
    String getRoute() {
        return this.route;
    }
    
    /**
     * TODO: javadoc
     * @return
     */
    public abstract Object handle();

    public final void set(RouteMatch match, HttpServletRequest servletRequest, HttpServletResponse response) {
        request = new Request(match.getHttpMethod(), servletRequest);
        this.response = response;
        params = setParams(match);
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name') 
     */
    protected final String params(String param) {
        if (param.startsWith(":")) {
            return params.get(param);
        } else {
            return params.get(":" + param);
        }
    }

    /**
     * Gets the raw response object handed in by Jetty
     */
    protected HttpServletResponse getRawResponse() {
        return response;
    }

    // TODO: implement error handling
    protected void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void returnType() {

    }

    protected void setContentType(String type) {
        response.setContentType(type);
    }

    private final Map<String, String> setParams(RouteMatch match) {
        LOG.info("set params for requestUri: "
                        + match.getRequestUri()
                        + ", matchUri: "
                        + match.getMatchUri());

        Map<String, String> params = new HashMap<String, String>();
        
        List<String> request = SparkUtils.convertRouteToList(match.getRequestUri());
        List<String> matched = SparkUtils.convertRouteToList(match.getMatchUri());

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                LOG.info("matchedPart: "
                                + matchedPart
                                + " = "
                                + request.get(i));
                params.put(matchedPart, request.get(i));
            }
        }
        return Collections.unmodifiableMap(params);
    }
}
