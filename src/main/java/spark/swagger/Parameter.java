package spark.swagger;

/**
 * Created by magrnw on 3/8/17.
 */
public class Parameter {
    private String name;
    private String description;
    private String schema;
    private Boolean required;
    private In in;
    private String type;

    public Parameter(String name) {
        this.name = name;
    }

    public enum Type {
        string,
        integer,
        array
    }

    public In getIn() {
        return in;
    }

    public Parameter in(In in) {
        this.in = in;
        return this;
    }

    public Parameter type(Type type) {
        this.type = type.name();
        return this;
    }

    public String getType() {
        return type;
    }

    public static enum In {
        body,
        query,
        path
    }

    public Parameter(String name, Class schema) {
        this.name = name;
        this.schema = schema.getName();
    }

    public String getName() {
        return name;
    }

    public Parameter name(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Parameter description(String description) {
        this.description = description;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public Parameter schema(String schema) {
        this.schema = schema;
        return this;
    }

    public Boolean getRequired() {
        return required;
    }

    public Parameter required() {
        this.required = true;
        return this;
    }
}
