package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class ExternalDocs {
    private String description;
    private String url;

    public ExternalDocs(String url) {
    }

    public ExternalDocs description(String description) {
        this.description = description;
        return this;
    }

    public ExternalDocs url(String url) {
        this.url = url;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}
