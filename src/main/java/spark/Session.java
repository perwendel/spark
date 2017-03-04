package spark;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import spark.utils.Assert;

/**
 * Provides session information.
 */
public class Session {

    private final Request request;
    private final HttpSession session;

    /**
     * Creates a session with the <code>HttpSession</code>.
     *
     * @param session
     * @param request
     * @throws IllegalArgumentException If the session or the request is null.
     */
    Session(HttpSession session, Request request) {
        Assert.notNull(session, "session cannot be null");
        Assert.notNull(request, "request cannot be null");
        this.session = session;
        this.request = request;
    }

    /**
     * @return the raw <code>HttpSession</code> object handed in by the servlet container.
     */
    public HttpSession raw() {
        return session;
    }

    /**
     * Returns the object bound with the specified name in this session, or null if no object is bound under the name.
     *
     * @param name a string specifying the name of the object
     * @param <T>  The type parameter
     * @return the object with the specified name
     */
    @SuppressWarnings("unchecked")
    public <T> T attribute(String name) {
        return (T) session.getAttribute(name);
    }

    /**
     * Binds an object to this session, using the name specified.
     *
     * @param name  the name to which the object is bound; cannot be null
     * @param value the object to be bound
     */
    public void attribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    /**
     * @return an <code>Enumeration</code> of <code>String</code> objects
     * containing the names of all the objects bound to this session.
     */
    public Set<String> attributes() {
        TreeSet<String> attributes = new TreeSet<>();
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            attributes.add(enumeration.nextElement());
        }
        return attributes;
    }

    /**
     * @return the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
     */
    public long creationTime() {
        return session.getCreationTime();
    }

    /**
     * @return a string containing the unique identifier assigned to this session.
     */
    public String id() {
        return session.getId();
    }

    /**
     * @return the last time the client sent a request associated with this session,
     * as the number of milliseconds since midnight January 1, 1970 GMT, and marked
     * by the time the container received the request.
     */
    public long lastAccessedTime() {
        return session.getLastAccessedTime();
    }

    /**
     * @return the maximum time interval, in seconds, that the container
     * will keep this session open between client accesses.
     */
    public int maxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    /**
     * Specifies the time, in seconds, between client requests the web container will invalidate this session.
     *
     * @param interval the interval
     */
    public void maxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }

    /**
     * Invalidates this session then unbinds any objects bound to it.
     */
    public void invalidate() {
        request.validSession(false);
        session.invalidate();
    }

    /**
     * @return true if the client does not yet know about the session or if the client chooses not to join the session.
     */
    public boolean isNew() {
        return session.isNew();
    }

    /**
     * Removes the object bound with the specified name from this session.
     *
     * @param name the name of the object to remove from this session
     */
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }
}