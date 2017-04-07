package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class Response {
    private String httpCode;
    private String description;

    public String getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
