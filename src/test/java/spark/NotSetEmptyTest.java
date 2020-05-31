package spark;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import static junit.framework.TestCase.assertEquals;
import static spark.Spark.get;


public class NotSetEmptyTest {
    //CS304 (manually written) Issue link: https://github.com/perwendel/spark/issues/1022
    /**
     * test the response that not set EmptyAvailable
     * if not set and without content type
     * the response will be set to default type
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
        http = new SparkTestUtil(4567);
        get("/null", (request, response) ->{return "null";} );
        Spark.awaitInitialization();
    }

    /**
     * header can find key "Content-Type", return true
     */
    @Test
    public void testServer(){
        try {
            SparkTestUtil.UrlResponse res=http.get("/null");
            assertEquals(200, res.status);
            assertEquals(true,res.headers.containsKey("Content-Type"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }




    }





}
