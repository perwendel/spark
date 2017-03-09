package spark.swagger;

/**
 * Created by magrnw on 3/9/17.
 */
public class RootTag {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public RootTag name(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RootTag description(String description) {
        this.description = description;
        return this;
    }
}
