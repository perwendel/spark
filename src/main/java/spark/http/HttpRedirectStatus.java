package spark.http;

/**
 * Http code for redirections : 3XX codes
 */
public enum HttpRedirectStatus implements HttpStatus {

    MULTIPLE_CHOICES(MULTIPLE_CHOICES_300, "Multiple Choices"),
    MOVED_PERMANENTLY(MOVED_PERMANENTLY_301, "Moved Permanently"),
    MOVED_TEMPORARILY(MOVED_TEMPORARILY_302, "Moved Temporarily"),
    FOUND(FOUND_302, "Found"),
    SEE_OTHER(SEE_OTHER_303, "See Other"),
    NOT_MODIFIED(NOT_MODIFIED_304, "Not Modified"),
    USE_PROXY(USE_PROXY_305, "Use Proxy"),
    TEMPORARY_REDIRECT(TEMPORARY_REDIRECT_307, "Temporary Redirect");

    private final int statusCode;
    private final String message;

    private HttpRedirectStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }


    public boolean equals(int statusCode) {
        return (this.statusCode == statusCode);
    }

    @Override
    public String toString() {
        return this.statusCode + this.getMessage();
    }
}
