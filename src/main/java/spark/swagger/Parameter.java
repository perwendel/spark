package spark.swagger;

/**
 * @author mattg
 */
public class Parameter {
    private String name;
    private String description;
    private Boolean required;
    private In in;
    private String type;
    private Schema schema;

    public Parameter(String name) {
        this.name = name;
    }

    public enum Type {
        string,
        array,
        integer,
        int64,
        int32,
        FLOAT,
        DOUBLE,
        BYTE,
        BINARY,
        DATE,
        DATE_TIME,
        PASSWORD
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
        path,
        header,
        formData
    }

    public Parameter(String name, Schema schema) {
        this.name = name;
        this.schema = schema;
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

    public Schema getSchema() {
        return schema;
    }

    public Parameter schema(Schema schema) {
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
