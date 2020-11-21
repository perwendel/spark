package spark.servlet;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

public class MyApp implements SparkApplication {
    static final String EXTERNAL_FILE = "externalFileServlet.html";

    @Override
    public synchronized void init() {
        before("/filter/protected/*", (request, response) -> {
            halt(401, "Go Away!");
        });

        get("/filter/hi", (request, response) -> "Hello World!");

        get("/filter/:param", (request, response) -> "echo: " + request.params(":param"));

        get("/filter/", (request, response) -> "Hello Root!");

        post("/filter/poster", (request, response) -> {
            String body = request.body();
            response.status(201); // created
            return "Body was: " + body;
        });

        after("/filter/hi", (request, response) -> response.header("after", "foobar"));

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
    }

}
