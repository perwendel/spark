package spark;

import org.junit.Before;
import org.junit.Test;
import spark.http.matching.Configuration;
import org.junit.Assert;

import static spark.Spark.*;

public class BeforeResponseTest {

    @Before
    public void setup() {
        Configuration.setDefaultcontentype("text/html");
    }

    /**.
     * change default response content type in configutaion
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void Testresponsedefaultcontenttype() throws Exception {
        get("/hello", (request, response) -> {
                Assert.assertEquals("text/html",response.type());
                return "Hello World!";
        }
        );
    }

    /**.
     * after change default content type, change one content type by filter.
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/911
     */
    @Test
    public void Testresponsedefaultcontenttypeaftermodified() throws Exception {
        before("/hello", (request, response) -> response.type("application/json"));
        get("/hello", (request, response) -> {
                Assert.assertEquals("text/html", Configuration.getDefaultcontentype());
                Assert.assertEquals("application/json",response.type());
                return "Hello World!";
            }
        );
    }
}
