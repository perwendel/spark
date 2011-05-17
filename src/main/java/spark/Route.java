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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.route.RouteMatch;

/**
 * 
 *
 * @author Per Wendel
 */
public abstract class Route {

    private static Logger LOG = Logger.getLogger(Route.class);

//    private HttpServletResponse response;

    protected Request request;

    private String route;
    
    protected Route(String route) {
        this.route = route;
    }
    
    /**
     * TODO: javadoc
     * @return
     */
    public abstract Object handle(Request request, Response response);

    String getRoute() {
        return this.route;
    }
    
    
    /**
     * Sets the needed information
     */
    public final void set(RouteMatch match, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        request = new Request(match, servletRequest);
        Response response = new Response(servletResponse);
//        this.response = response;
    }

    
}
