package spark.webserver;

import spark.session.ISessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class SparkHttpRequestWrapper extends HttpRequestWrapper {
    private ISessionStrategy sessionStrategy;
    private HttpSession session;

    public SparkHttpRequestWrapper(HttpServletRequest request, ISessionStrategy sessionStrategy) {
        super(request);
        this.sessionStrategy = sessionStrategy;
    }

    public boolean isSessionInstantiated() {
        return session != null;
    }

    public void persistSession(HttpServletResponse response) throws IOException {
        if (isSessionInstantiated()) {
            sessionStrategy.writeSession(this, response);
        }
    }

    @Override
    public HttpSession getSession() {
        if (session == null) {
            session = sessionStrategy.getSession((HttpServletRequest) getRequest(), true);
        }
        return session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null) {
            session = sessionStrategy.getSession((HttpServletRequest) getRequest(), create);
        }
        return session;
    }
}
