package spark;

import org.junit.AfterClass;

public abstract class SparkBaseTest {

    @AfterClass
    public static void afterClass() throws Exception {
        Spark.stop();
        Spark.awaitStop();
    }
}
