package spark;

import javax.servlet.http.HttpSession;

public class Session {

    private HttpSession session;

    public Session(HttpSession session) {
        this.session = session;
    }

    public void set(String name, Object value) {
        session.setAttribute(name, value);
    }

    public Object get(String name) {
        return session.getAttribute(name);
    }
}
