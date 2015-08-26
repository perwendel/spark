package spark.webserver.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Tim Heinrich on 26.08.2015.
 *
 * Default implementation for the session provided by Jetty
 */
public class JettySessionCreationStrategy implements ISessionCreationStrategy{
    @Override
    public HttpSession getSession(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }
}
