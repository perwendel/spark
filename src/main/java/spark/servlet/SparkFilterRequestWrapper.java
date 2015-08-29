package spark.servlet;

import spark.session.ISessionStrategy;
import spark.webserver.SparkHttpRequestWrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Tim Heinrich on 26.08.2015.
 */
public class SparkFilterRequestWrapper extends SparkHttpRequestWrapper {
    private final String relativePath;

    public SparkFilterRequestWrapper(HttpServletRequest request, ISessionStrategy sessionStrategy, String relativePath) {
        super(request, sessionStrategy);
        this.relativePath = relativePath;
    }

    @Override
    public String getPathInfo() {
        return relativePath;
    }

    @Override
    public String getRequestURI() {
        return relativePath;
    }
}
