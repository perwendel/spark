package spark.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public interface ISessionCreationStrategy {
    /**
     * Get the session implementation. Result should be cached
     * @param create If true the session should be created if it does not exist; false othewise.
     * @param request The current request
     * @return The session; null if create is false and session does not exist.
     */
    HttpSession getSession(HttpServletRequest request, boolean create);
}
