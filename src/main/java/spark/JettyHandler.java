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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.log.Log;


class JettyHandler extends AbstractHandler {
    private MatcherFilter filter;
    public JettyHandler() {
        filter = new MatcherFilter();
        filter.init(null);
    }

    public boolean handle(String target, HttpServletRequest request, HttpServletResponse response,
                    int arg3) throws IOException, ServletException {
        Log.info("jettyhandler, handle();");
        filter.doFilter(request, response, null);
        return true;
    }

}