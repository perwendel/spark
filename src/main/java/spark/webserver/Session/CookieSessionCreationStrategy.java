package spark.webserver.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class CookieSessionCreationStrategy implements ISessionCreationStrategy {
    @Override
    public HttpSession getSession(HttpServletRequest request, boolean create) {
        CookieSession cookieSession = new CookieSession(request);
        return cookieSession;
    }
}
