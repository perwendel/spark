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

/**
 * 
 *
 * @author Per Wendel
 */
public interface RouteMatcher {
    
    /**
     * 
     * @param route
     * @param target
     */
    void parseValidateAddRoute(String route, Object target);
    
    /**
     * 
     * @param httpMethod
     * @param route
     * @return
     */
    RouteMatch findTargetForRequestedRoute(HttpMethod httpMethod, String route);
}
