package spark.servlet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;

public class ServletTest extends AbstractServletTest {
    public ServletTest() {
        super("servlet");
    }

    @BeforeClass
    public static void setup() throws InterruptedException {
        absSetup();
    }

    @AfterClass
    public static void tearDown() {
        absTearDown();
    }

    @Override
    protected String getExternalFileName() {
        return MyServletServedApp2.EXTERNAL_FILE;
    }

    @Test
    public void testExternalStaticFile() throws Exception {
        createTempStaticFile("servlet", getExternalFileName());
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET",
                "/somepath/servlet/" + getExternalFileName(),
                null);
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("Content of external file", response.body);
    }

}
