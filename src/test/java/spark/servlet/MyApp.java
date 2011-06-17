package spark.servlet;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

    @Override
    public void init() {
        System.out.println("HELLO!!!");
        before(new Filter("/protected/*") {

            @Override
            public void handle(Request request, Response response) {
                halt(401, "Go Away!");
            }
        });

        get(new Route("/hi") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });

        get(new Route("/:param") {

            @Override
            public Object handle(Request request, Response response) {
                return "echo: " + request.params(":param");
            }
        });

        get(new Route("/") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello Root!";
            }
        });

        post(new Route("/poster") {

            @Override
            public Object handle(Request request, Response response) {
                String body = request.body();
                response.status(201); // created
                return "Body was: " + body;
            }
        });

        after(new Filter("/hi") {

            @Override
            public void handle(Request request, Response response) {
                response.header("after", "foobar");
            }
        });

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

}
