package spark;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.util.SparkTestUtil;

import static spark.Spark.*;

/**
 * Basic test to ensure that multiple before and after filters can be mapped to a route.
 */
public class MultipleFiltersTest {
    private static SparkTestUtil http;
    private static class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class LoadUser implements Filter {
        @Override
        public void handle(Request request, Response response) throws Exception {
            User u = new User();
            u.setName("Kevin");
            request.attribute("user", u);
        }
    }
    private static class SetCounter implements Filter {
        @Override
        public void handle(Request request, Response response) throws Exception {
            request.attribute("counter", 0);
        }
    }

    private static class IncrementCounter implements Filter {
        @Override
        public void handle(Request request, Response response) throws Exception {
            int counter = request.attribute("counter");
            counter++;
            request.attribute("counter", counter);
        }
    }

    @BeforeClass
    public static void setup() {
        http = new SparkTestUtil(4567);

        before("/user", new SetCounter(), new IncrementCounter(), new LoadUser());

        after("/user", new IncrementCounter(), (req, res) -> {
            int counter = req.attribute("counter");
            Assert.assertEquals(counter, 2);
        });

        get("/user", (request, response) -> {
            Assert.assertEquals((int)request.attribute("counter"), 1);
            return ((User) request.attribute("user")).getName();
        });

        Spark.awaitInitialization();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void testMultipleFilters() {
        try {
            SparkTestUtil.UrlResponse response = http.get("/user");
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Kevin", response.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
