/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark.webserver;

import spark.route.RouteMatcherFactory;

/**
 * 
 *
 * @author Per Wendel
 */
public class SparkServerFactory {

    /**
     * 
     * @return
     */
    public static SparkServer create() {
        MatcherFilter matcherFilter = new MatcherFilter(RouteMatcherFactory.get());
        matcherFilter.init(null);
        JettyHandler handler = new JettyHandler(matcherFilter);
        return new SparkServerImpl(handler);
    }
    
}
