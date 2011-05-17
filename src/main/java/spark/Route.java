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


/**
 * A Route is built up by a path (for url-matching) and the implementation of the 'handle' method.
 * When a request is made, if present, the matching routes 'handle' method is invoked. 
 *
 * @author Per Wendel
 */
public abstract class Route {

    private String path;
    
    /**
     * Constructor
     * 
     * @param path The route path which is used for matching. (e.g. /hello, users/:name) 
     */
    protected Route(String path) {
        this.path = path;
    }
    
    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     * 
     * @param request The request object providing information about the HTTP request
     * @param response The response object providing functionality for modifying the response
     * 
     * @return The content to be set in the response
     */
    public abstract Object handle(Request request, Response response);

    /**
     * Returns this route's path
     */
    String getPath() {
        return this.path;
    }
    
}
