package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class License {
    private String name;
    private String url;

    public String getUrl() {
        return url;
    }

    public License url(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public License name(String name) {
        this.name = name;
        return this;
    }
}
