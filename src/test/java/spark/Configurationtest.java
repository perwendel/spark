package spark;

import org.junit.Assert;
import org.junit.Test;
import spark.http.matching.Configuration;
import spark.util.SparkTestUtil;


public class Configurationtest {
    /**.
     * test get method.
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void gettypetruetest() throws Exception {
        Configuration.setDefaultcontentype("application/json");
        Assert.assertEquals("application/json",Configuration.getDefaultcontentype());
    }

    /**.
     * test get method.
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void gettypefalsetest() throws Exception {
        Configuration.setDefaultcontentype("application/json");
        Assert.assertNotEquals("text/html",Configuration.getDefaultcontentype());
    }

    /**.
     * test set method.
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void settypetruetest() throws Exception {
        Configuration.setDefaultcontentype("text/html");
        Assert.assertEquals("text/html",Configuration.getDefaultcontentype());
    }

    /**.
     * test set method.
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void settypefalsetest() throws Exception {
        Configuration.setDefaultcontentype("text/html");
        Assert.assertNotEquals("application/json",Configuration.getDefaultcontentype());
    }
}
