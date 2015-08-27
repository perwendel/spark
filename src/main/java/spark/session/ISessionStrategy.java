package spark.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public interface ISessionStrategy {
    /**
     * Get the session implementation. Result should be cached
     * @param create If true the session should be created if it does not exist; false othewise.
     * @param request The current request
     * @return The session; null if create is false and session does not exist.
     */
    HttpSession getSession(HttpServletRequest request, boolean create);

    /**
     * Persists the session. Depending on the session strategy this may for instance be the JettyServer or the Cookie
     * @param request The current request
     * @param response The current response
     * @throws IOException
     */
    void writeSession(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
