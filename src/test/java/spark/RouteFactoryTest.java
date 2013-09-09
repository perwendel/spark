package spark;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.isReady;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.util.SparkTestUtil;
import spark.util.SparkTestUtil.UrlResponse;

public class RouteFactoryTest {
    static SparkTestUtil testUtil;
    static File tmpExternalFile;

    @AfterClass
    public static void tearDown() {
        Spark.clearRoutes();
        Spark.stop();
        if (tmpExternalFile != null) {
            tmpExternalFile.delete();
        }
    }

    public static class HellowWorldJsonRoute extends Route {

        protected HellowWorldJsonRoute() {
            super(null);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Object handle(Request request, Response response) {
            return "{\"message\": \"Hello World\"}";
        }

    }

    public static class HellowWorldTextRoute extends Route {

        protected HellowWorldTextRoute() {
            super(null);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Object handle(Request request, Response response) {
            return "Hello World!";
        }

    }

    @BeforeClass
    public static void setup() throws IOException {
        testUtil = new SparkTestUtil(4567);

        get(new RouteFactory("/hi", "application/json",
                HellowWorldJsonRoute.class));
        get(new RouteFactory("/hi", HellowWorldTextRoute.class));
        // get(new Route("/hi") {
        // @Override
        // public Object handle(Request request, Response response) {
        // return "Hello World!";
        // }
        // });
        // get(new Route("/param/:param") {
        // @Override
        // public Object handle(Request request, Response response) {
        // return "echo: " + request.params(":param");
        // }
        // });
        // get(new Route("/paramandwild/:param/stuff/*") {
        // @Override
        // public Object handle(Request request, Response response) {
        // return "paramandwild: " + request.params(":param")
        // + request.splat()[0];
        // }
        // });
        // get(new Route("/paramwithmaj/:paramWithMaj") {
        // @Override
        // public Object handle(Request request, Response response) {
        // return "echo: " + request.params(":paramWithMaj");
        // }
        // });
        // get(new TemplateViewRoute("/templateView") {
        // @Override
        // public String render(ModelAndView modelAndView) {
        // return modelAndView.getModel() + " from "
        // + modelAndView.getViewName();
        // }
        //
        // @Override
        // public ModelAndView handle(Request request, Response response) {
        // return new ModelAndView("Hello", "my view");
        // }
        // });
        // get(new Route("/") {
        // @Override
        // public Object handle(Request request, Response response) {
        // return "Hello Root!";
        // }
        // });
        // post(new Route("/poster") {
        // @Override
        // public Object handle(Request request, Response response) {
        // String body = request.body();
        // response.status(201); // created
        // return "Body was: " + body;
        // }
        // });
        // patch(new Route("/patcher") {
        // @Override
        // public Object handle(Request request, Response response) {
        // String body = request.body();
        // response.status(200);
        // return "Body was: " + body;
        // }
        // });
        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                response.header("after", "foobar");
            }
        });
        Integer lock = new Integer(3);
        while (lock > 0 && !isReady()) {
            try {
                synchronized (lock) {
                    lock.wait(2000);
                }

                lock--;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!isReady()) {
            Assert.fail("Spark server still not ready");
        }
    }

    @Test
    public void routes_should_be_accept_type_aware() throws Exception {
        UrlResponse response = testUtil.doMethod("GET", "/hi", null,
                "application/json");
        Assert.assertEquals(200, response.status);
        Assert.assertEquals("{\"message\": \"Hello World\"}", response.body);
    }

    @Test
    public void testGetHi() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("Hello World!", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHiHead() {
        try {
            UrlResponse response = testUtil.doMethod("HEAD", "/hi", null);
            Assert.assertEquals(200, response.status);
            Assert.assertEquals("", response.body);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetHiAfterFilter() {
        try {
            UrlResponse response = testUtil.doMethod("GET", "/hi", null);
            Assert.assertTrue(response.headers.get("after").contains("foobar"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
