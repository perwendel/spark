package spark.route;

import java.util.HashMap;
import java.util.Set;

public class SparkResponse {
    private final String routeSourceUrl;
    private final HashMap<String, Object> targets;
    private final Object defaultTarget;

    public SparkResponse(String routeSourceUrl, HashMap<String, Object> targets, Object defaultTarget) {
        this.routeSourceUrl = routeSourceUrl;
        this.targets = targets;
        this.defaultTarget = defaultTarget;
    }

    public String getRouteSourceUrl() {
        return this.routeSourceUrl;
    }

    public Set<String> getAcceptedMimeTypes() {
        return this.targets.keySet();
    }

    public Object getTargetForAcceptType(String acceptType) {
        return this.targets.get(acceptType);
    }

    public Object getDefaultTarget() {
        return this.defaultTarget;
    }
}
