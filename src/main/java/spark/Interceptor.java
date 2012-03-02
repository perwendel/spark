package spark;

import spark.utils.SparkUtils;

public abstract class Interceptor extends AbstractRoot {

    private String path;
    
    /**
     * Constructs an Interceptor that matches on everything
     */
    protected Interceptor() {
        this.path = SparkUtils.ALL_PATHS;
    }
    
    /**
     * Constructor
     * 
     * @param path The interceptor path which is used for matching. (e.g. /hello, users/:name) 
     */
    protected Interceptor(String path) {
        this.path = path;
    }
    
    /**
     * Invoked when a request is made on this interceptorr's corresponding path e.g. '/hello'
     * 
     * @param request The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     */
    public abstract void handle(Request request, Response response, InterceptorChain chain);

    /**
     * Returns this route's path
     */
    String getPath() {
        return this.path;
    }

}
