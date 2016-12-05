package spark.embeddedserver.jetty.logger;

import org.eclipse.jetty.server.RequestLog;

/**
 * A wrapper for Jetty's request log
 */
public interface RequestLogWrapper {

    /**
     * Gets the actual handler - if necessary, instantiating an object.
     *
     * @return The handler instance.
     */
    Object getRequestLog();

    static void validateRequestLogClass(Class<?> requestLogClass) {
        boolean valid = RequestLog.class.isAssignableFrom(requestLogClass);
        if (!valid) {
            throw new IllegalArgumentException(
                    "RequestLog instance must implement 'RequestLog'");
        }
    }

}
