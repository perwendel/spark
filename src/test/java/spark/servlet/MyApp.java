package spark.servlet;

import spark.*;

public class MyApp implements SparkApplication {

    @Override
    public void init(SparkInstance spark) {
        System.out.println("HELLO!!!");
        spark.before(new Filter("/protected/*") {

            @Override
            public void handle(Request request, Response response) {
                halt(401, "Go Away!");
            }
        });

        spark.get(new Route("/hi") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });

        spark.get(new Route("/:param") {

            @Override
            public Object handle(Request request, Response response) {
                return "echo: " + request.params(":param");
            }
        });

        spark.get(new Route("/") {

            @Override
            public Object handle(Request request, Response response) {
                return "Hello Root!";
            }
        });

        spark.post(new Route("/poster") {

            @Override
            public Object handle(Request request, Response response) {
                String body = request.body();
                response.status(201); // created
                return "Body was: " + body;
            }
        });

        spark.after(new Filter("/hi") {

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
