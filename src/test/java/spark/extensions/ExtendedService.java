package spark.extensions;

import spark.AbstractService;

/**
 * @author David Åse
 */
public class ExtendedService extends AbstractService<ExtendedService> {

    public void get(String path, EmptyRoute route) {
        super.get(path, route);
    }

    public static ExtendedService ignite() {
        return new ExtendedService();
    }

}
