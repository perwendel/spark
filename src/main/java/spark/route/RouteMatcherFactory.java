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

import org.apache.log4j.Logger;

/**
 * 
 *
 * @author Per Wendel
 */
public class RouteMatcherFactory {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(RouteMatcherFactory.class);

    private static RouteMatcher routeMatcher = null;

    public static synchronized RouteMatcher get() {
        if (routeMatcher == null) {
            LOG.debug("creates RouteMatcher");
            routeMatcher = new RouteMatcherImpl();
        }
        return routeMatcher;
    }

}
