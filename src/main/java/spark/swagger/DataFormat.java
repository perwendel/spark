package spark.swagger;

/**
 * The following describe the data types associated with the swagger types for specifying objects
 *
 * @author mattg
 */
public enum DataFormat {
    INT32("int32"),
    INT64("int64"),
    FLOAT("float"),
    DOUBLE("double"),
    BYTE("byte"),
    BINARY("binary"),
    DATE("date"),
    DATE_TIME("date-time"),
    PASSWORD("password");

    private final String name;

    DataFormat(String name) {
        this.name = name;
    }

    public String getDataName() {
        return name;
    }
}
