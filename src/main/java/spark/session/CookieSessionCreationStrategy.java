package spark.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class CookieSessionCreationStrategy implements ISessionCreationStrategy {
    private CookieSession session;

    @Override
    public HttpSession getSession(HttpServletRequest request, boolean create) {
        if (session == null) {
            try {
                session = CookieSessionHandler.readSession(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return session;
    }
}
