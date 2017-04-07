package spark.swagger;

import java.util.HashSet;

/**
 * Created by magrnw on 3/9/17.
 */
public class Tags extends HashSet<String> {

    public Tags tag(String name) {
        this.add(name);
        return this;
    }
}
