package spark.swagger;

import java.util.ArrayList;

/**
 * Created by magrnw on 3/8/17.
 */
public class Parameters extends ArrayList<Parameter> {
    public Parameters parameter(Parameter parameter) {
        this.add(parameter);
        return this;
    }
}
