package spark;

/**
 * Created by Marty Lamb on 2015-03-17.
 */
public interface SimpleRoute {

    /**
     * Invoked when a request is made on this route's corresponding path e.g. '/hello'
     */
    Object handle()  throws Exception;

}
