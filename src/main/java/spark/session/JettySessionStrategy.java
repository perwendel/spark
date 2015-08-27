package spark.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 *
 * Default implementation for the session provided by Jetty
 */
public class JettySessionStrategy implements ISessionStrategy {
    @Override
    public HttpSession getSession(HttpServletRequest request, boolean create) {
        return request.getSession(create);
    }

    /**
     * Persists the session. Depending on the session strategy this may for instance be the JettyServer or the Cookie
     *
     * @param request  The current request
     * @param response The current response
     * @throws IOException
     */
    @Override
    public void writeSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Nothing to do here, this is handled by Jetty
    }
}
