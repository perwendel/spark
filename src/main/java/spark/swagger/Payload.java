package spark.swagger;

import java.util.HashSet;

/**
 * Created by magrnw on 3/8/17.
 */
public class Payload extends HashSet<String> {
    public static final String JSON = "application/json";
    public static final String XML = "application/xml";

    public Payload xml() {
        this.add(XML);
        return this;
    }

    public Payload json() {
        this.add(JSON);
        return this;
    }
}
