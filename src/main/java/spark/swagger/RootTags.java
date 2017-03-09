package spark.swagger;

import java.util.HashSet;

/**
 * Created by magrnw on 3/9/17.
 */
public class RootTags extends HashSet<RootTag> {
    public RootTags tag(String name, String description) {
        this.add(new RootTag().name(name).description(description));
        return this;
    }
}
