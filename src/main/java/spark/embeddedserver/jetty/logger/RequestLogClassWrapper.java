package spark.embeddedserver.jetty.logger;

import static java.util.Objects.requireNonNull;

public class RequestLogClassWrapper implements RequestLogWrapper {

    private final Class<?> requestLogClass;

    public RequestLogClassWrapper(Class<?> requestLogClass) {
        requireNonNull(requestLogClass, "Request log class cannot be null");
        RequestLogWrapper.validateRequestLogClass(requestLogClass);
        this.requestLogClass = requestLogClass;
    }


    @Override
    public Object getRequestLog() {
        try {
            return requestLogClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Could not instantiate request log", ex);
        }
    }

}
