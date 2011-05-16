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


public class RouteMatch {

    private HttpMethod httpMethod;
    private Object target;
    private String matchUri;
    private String requestUri;
    
    public RouteMatch(HttpMethod httpMethod, Object target, String matchUri, String requestUri) {
        super();
        this.httpMethod = httpMethod;
        this.target = target;
        this.matchUri = matchUri;
        this.requestUri = requestUri;
    }

    
    /**
     * @return the httpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    /**
     * @return the target
     */
    public Object getTarget() {
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
