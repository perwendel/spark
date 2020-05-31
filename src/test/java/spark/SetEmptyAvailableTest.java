package spark;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import static junit.framework.TestCase.assertEquals;
import static spark.Spark.*;

public class SetEmptyAvailableTest {
    /**
     * CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/1022
     * test the response that set EmptyAvailable
     * if set and without content type
     * the response will have no content type in header
     */
    private static SparkTestUtil http;

    /**
     * stop the SparkTestUtil
     */
    @AfterClass
    public static void tearDown() {
        Spark.stop();
    }

    /**
     * set a SparkTestUtil for testing
     */
    @BeforeClass
    public static void setup(){
        http=new SparkTestUtil(4567);
        setEmptyAvailable();
        get("/null",(request, response) ->{return "null";} );
        Spark.awaitInitialization();
    }

    /**
     * header can not find key "Content-Type", return false
     */
    @Test
    public void testNotSetEmpty(){
        try {
            SparkTestUtil.UrlResponse res=http.get("/null");
            assertEquals(200, res.status);
            assertEquals(false,res.headers.containsKey("Content-Type"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }
}
