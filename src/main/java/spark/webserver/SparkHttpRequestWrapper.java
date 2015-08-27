package spark.webserver;

import spark.session.CookieSessionCreationStrategy;
import spark.session.ISessionCreationStrategy;
import spark.session.JettySessionCreationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class SparkHttpRequestWrapper extends HttpRequestWrapper {
    private ISessionCreationStrategy sessionCreationStrategy;
    private HttpSession session;

    public SparkHttpRequestWrapper(HttpServletRequest request, boolean clientSession) {
        super(request);
        if (clientSession) {
            sessionCreationStrategy = new CookieSessionCreationStrategy();
        } else {
            sessionCreationStrategy = new JettySessionCreationStrategy();
        }
    }

    public boolean isSessionInstantiated() {
        return session != null;
    }

    @Override
    public HttpSession getSession() {
        if (session == null) {
            session = sessionCreationStrategy.getSession((HttpServletRequest) getRequest(), true);
        }
        return session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null) {
            session = sessionCreationStrategy.getSession((HttpServletRequest) getRequest(), create);
        }
        return session;
    }
}
