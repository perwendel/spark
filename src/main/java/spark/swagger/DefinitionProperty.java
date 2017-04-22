package spark.swagger;

/**
 * Created by magrnw on 4/21/17.
 */
public class DefinitionProperty {
    private String type;
    private String format;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format.getDataName();
    }

    public String getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type.getType();
    }
}
