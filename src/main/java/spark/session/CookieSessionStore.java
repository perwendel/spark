package spark.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public class CookieSessionStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Object> attributes = new HashMap<>();
    private long creationTime;
    private String id;
    private long lastAccessedTime;
    private boolean isNew;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public boolean isNew() {
        return isNew;
    }
}
