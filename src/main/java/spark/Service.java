package spark;

/**
 * @author David Åse
 */
public class Service extends AbstractService<Service>{
    /**
     * Creates a new Service (a Spark instance). This should be used instead of the static API if the user wants
     * multiple services in one process.
     */
    public static Service ignite() {
        return new Service();
    }
}
