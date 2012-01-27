package spark;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

public class Session {

    private HttpSession session;

    /**
     * 
     * @param session
     * @throws NullPointerException If the session is null.
     */
    Session(HttpSession session) throws NullPointerException {
        if (session == null) {
            throw new NullPointerException();
        }
        this.session = session;
    }

    public HttpSession raw() {
        return session;
    }

    @SuppressWarnings("unchecked")
    public <T> T attribute(String name) {
        return (T) session.getAttribute(name);
    }

    /**
     * Binds an object to this session, using the name specified.
     * 
     * @param name
     * @param value
     */
    public void attribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    @SuppressWarnings("unchecked")
    public Set<String> attributes() {
        TreeSet<String> attributes = new TreeSet<String>();
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            attributes.add(enumeration.nextElement());
        }
        return attributes;
    }

    public long creationTime() {
        return session.getCreationTime();
    }

    public String id() {
        return session.getId();
    }

    public long lastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public int maxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    /**
     * Specifies the time, in seconds, between client requests the web container will invalidate this session.
     * 
     * @param interval
     */
    public void maxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }
}
