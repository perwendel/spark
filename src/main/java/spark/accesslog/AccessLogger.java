package spark.accesslog;

import org.eclipse.jetty.server.RequestLog;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Configure access logs programmatically as per
 * http://www.eclipse.org/jetty/documentation/current/configuring-jetty-request-logs.html#implementing-request-log
 */
public class AccessLogger {
    public final Optional<RequestLog> accessLog;

    public AccessLogger() {
        this(empty());
    }
    public AccessLogger(Optional<RequestLog> accessLog) {
        this.accessLog = accessLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessLogger that = (AccessLogger) o;
        return Objects.equals(accessLog, that.accessLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessLog);
    }
}
