package spark.route;

import com.augustl.pathtravelagent.*;
import com.augustl.pathtravelagent.RouteMatch;

import java.util.HashMap;

public class SparkRequestHandler implements IRouteHandler<SparkRequest, SparkResponse> {
    private final String routeSourceUrl;
    private final HashMap<HttpMethod, HashMap<String, Object>> handlers = new HashMap<HttpMethod, HashMap<String, Object>>();
    private Object defaultTarget;

    public SparkRequestHandler(String routeSourceUrl) {
        this.routeSourceUrl = routeSourceUrl;
    }

    public void addMethodAndAcceptHandler(HttpMethod method, String acceptedType, Object target) {
        if (defaultTarget == null) {
            this.defaultTarget = target;
        }

        if (!handlers.containsKey(method)) {
            handlers.put(method, new HashMap<String, Object>());
        }

        // Warn/throw if there's already a handler for acceptedType?
        handlers.get(method).put(acceptedType, target);
    }

    @Override
    public SparkResponse call(RouteMatch<SparkRequest> match) {
        HttpMethod requestMethod = match.getRequest().getMethod();
        if (this.handlers.containsKey(requestMethod)) {
            return new SparkResponse(this.routeSourceUrl, this.handlers.get(requestMethod), this.defaultTarget);
        } else {
            return null;
        }
    }
}
