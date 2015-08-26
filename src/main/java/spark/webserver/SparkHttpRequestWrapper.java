package spark.webserver;

import spark.webserver.session.CookieSessionCreationStrategy;
import spark.webserver.session.ISessionCreationStrategy;
import spark.webserver.session.JettySessionCreationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class SparkHttpRequestWrapper extends HttpRequestWrapper {
    private ISessionCreationStrategy sessionCreationStrategy;

    public SparkHttpRequestWrapper(HttpServletRequest request, boolean clientSession) {
        super(request);
        if (clientSession) {
            sessionCreationStrategy = new CookieSessionCreationStrategy();
        } else {
            sessionCreationStrategy = new JettySessionCreationStrategy();
        }
    }

    @Override
    public HttpSession getSession() {
        return sessionCreationStrategy.getSession((HttpServletRequest) getRequest(), true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        return sessionCreationStrategy.getSession((HttpServletRequest) getRequest(), create);
    }
}
