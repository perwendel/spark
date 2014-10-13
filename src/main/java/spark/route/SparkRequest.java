package spark.route;

import com.augustl.pathtravelagent.IRequest;

public class SparkRequest implements IRequest {
    private final HttpMethod method;
    private final String path;
    public SparkRequest(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
