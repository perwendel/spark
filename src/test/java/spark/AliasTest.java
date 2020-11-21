package spark;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static spark.Spark.*;

public class AliasTest {

    private static SparkTestUtil testUtil;

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        staticFileLocation("textfiles");
        alias.get("/hi", "/hello.txt","textfiles");

        Spark.awaitInitialization();
    }

    @Test
    public void testRedirectGet() throws Exception {
        SparkTestUtil.UrlResponse response = testUtil.doMethod("GET", "/hi", null);
        Assert.assertEquals(200, response.status);
        BufferedReader br = new BufferedReader(new FileReader("src/main/resources/textfiles/hello.txt"));
        StringBuilder content = new StringBuilder((char)br.read()+"");
        while(br.ready())
            content.append((char)br.read());
        Assert.assertEquals(content.toString(), response.body);
        br.close();
    }
}
