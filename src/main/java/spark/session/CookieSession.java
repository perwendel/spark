package spark.session;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;


/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class CookieSession implements HttpSession, Serializable {
    private static final long serialVersionUID = 1L;

    private CookieSessionStore session = new CookieSessionStore();
    private transient HttpServletRequest request;

    public CookieSession() { }

    public CookieSession(HttpServletRequest request) {
        this.request = request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Map<String, Object> getAttributes() {
        return session.getAttributes();
    }

    @Override
    public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override
    public String getId() {
        return session.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        throw new NotImplementedException();
    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    /**
     * @deprecated
     */
    @Override
    public HttpSessionContext getSessionContext() {
        return request.getSession().getSessionContext();
    }

    @Override
    public Object getAttribute(String s) {
        return session.getAttributes().get(s);
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public Object getValue(String s) {
        return session.getAttributes().get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return (Enumeration<String>) session.getAttributes().keySet();
    }

    /**
     * @deprecated
     */
    @Override
    public String[] getValueNames() {
        return session.getAttributes().keySet().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String s, Object o) {
        session.getAttributes().put(s, o);
    }

    /**
     * @param s
     * @param o
     * @deprecated
     */
    @Override
    public void putValue(String s, Object o) {
        setAttribute(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        session.getAttributes().remove(s);
    }

    /**
     * @param s
     * @deprecated
     */
    @Override
    public void removeValue(String s) {
        removeAttribute(s);
    }

    @Override
    public void invalidate() {
        session.getAttributes().clear();
    }

    @Override
    public boolean isNew() {
        return session.isNew();
    }
}
