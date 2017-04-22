package spark.swagger;

/**
 * The type of a parameter
 *
 * @author mattg
 */
public enum DataType {
    INTEGER("integer"),
    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean");

    private final String type;

    DataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
