package spark.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tim Heinrich on 27.08.2015.
 */
public class CookieSessionStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Object> attributes = new HashMap<>();
    private long creationTime;
    private String id;
    private long lastAccessedTime;
    private boolean isNew = true;

    public CookieSessionStore() {
        id = UUID.randomUUID().toString();
    }

    public Map<String, Object> getAttributes() {
        lastAccessedTime = new Date().getTime();
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        lastAccessedTime = new Date().getTime();
        this.attributes = attributes;
    }

    public long getCreationTime() {
        lastAccessedTime = new Date().getTime();
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        lastAccessedTime = new Date().getTime();
        this.creationTime = creationTime;
    }

    public String getId() {
        lastAccessedTime = new Date().getTime();
        return id;
    }

    public void setId(String id) {
        lastAccessedTime = new Date().getTime();
        this.id = id;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public boolean isNew() {
        lastAccessedTime = new Date().getTime();
        return isNew;
    }

    void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
