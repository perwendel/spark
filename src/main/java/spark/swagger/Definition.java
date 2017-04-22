package spark.swagger;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

/**
 * Defines the associated definition for a specific object entity.
 *
 * @author mattg
 */
public class Definition {
    private String type = "object";
    private HashMap<String, DefinitionProperty> properties = new HashMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, DefinitionProperty> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, DefinitionProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(Field field) {
        DefinitionProperty property = new DefinitionProperty();
        if (field.getType() == String.class) {
            property.setType(DataType.STRING);
        } else if (field.getType() == Float.class) {
            property.setType(DataType.NUMBER);
            property.setFormat(DataFormat.FLOAT);
        } else if (field.getType() == Double.class) {
            property.setType(DataType.NUMBER);
            property.setFormat(DataFormat.DOUBLE);
        } else if (field.getType() == Long.class) {
            property.setType(DataType.INTEGER);
            property.setFormat(DataFormat.INT64);
        } else if (field.getType() == Integer.class) {
            property.setType(DataType.INTEGER);
            property.setFormat(DataFormat.INT32);
        } else if (field.getType() == Boolean.class) {
            property.setType(DataType.BOOLEAN);
        } else if (field.getType() == byte.class) {
            property.setType(DataType.STRING);
            property.setFormat(DataFormat.BYTE);
        } else if (field.getType() == Date.class) {
            property.setType(DataType.STRING);
            property.setFormat(DataFormat.DATE_TIME);
        } else {
            property = null;
        }

        if (null != property) {
            properties.put(field.getName(), property);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Definition that = (Definition) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
