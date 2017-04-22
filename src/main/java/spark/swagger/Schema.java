package spark.swagger;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

/**
 * Defines both the schema and the definition of that schema element.
 *
 * @author mattg
 */
public class Schema {
    // This should be serialized as "$ref" = #/definitions/ClassNameSimple
    @SerializedName("$ref")
    private String ref;

    public Schema() {}

    public Schema(Class ref) {
        this.ref(ref);
    }

    private transient Definition definition;
    private transient String className;


    public Schema ref(Class clazz) {
        this.ref = "#/definitions/" + clazz.getSimpleName();
        this.className = clazz.getSimpleName();

        definition = new Definition();
        for (Field field : clazz.getDeclaredFields()) {
            definition.addProperty(field);
        }
        return this;
    }

    public Definition getDefinition() {
        return definition;
    }

    public String getRef() {
        return ref;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schema schema = (Schema) o;

        if (ref != null ? !ref.equals(schema.ref) : schema.ref != null) return false;
        return definition != null ? definition.equals(schema.definition) : schema.definition == null;
    }

    @Override
    public int hashCode() {
        int result = ref != null ? ref.hashCode() : 0;
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        return result;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
