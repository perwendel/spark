package spark.extensions;

import spark.Spark;

/**
 * @author David Åse
 */
public class ExtendedSpark extends Spark {

    public static void get(String path, EmptyRoute route) {
        getInstance().get(path, route);
    }

}
