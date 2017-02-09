package spark.embeddedserver.jetty.logger;

import static java.util.Objects.requireNonNull;

public class RequestLogInstanceWrapper implements RequestLogWrapper {

    private final Object requestLog;

    public RequestLogInstanceWrapper(Object requestLog) {
        requireNonNull(requestLog, "Request log cannot be null");
        RequestLogWrapper.validateRequestLogClass(requestLog.getClass());
        this.requestLog = requestLog;
    }

    @Override
    public Object getRequestLog() {
        return requestLog;
    }

}
