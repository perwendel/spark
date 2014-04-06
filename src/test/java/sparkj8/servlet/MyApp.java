package sparkj8.servlet;

import static spark.SparkJ8.after;
import static spark.SparkJ8.before;
import static spark.SparkJ8.get;
import static spark.SparkJ8.post;

import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

    @Override public void init () {
        System.out.println ("HELLO!!!");

        before ("/protected/*", (it, request, response) -> it.halt (401, "Go Away!"));

        get ("/hi", (it, request, response) -> "Hello World!");

        get ("/:param", (it, request, response) -> "echo: " + request.params (":param"));

        get ("/", (it, request, response) -> "Hello Root!");

        post ("/poster", (it, request, response) -> {
            String body = request.body ();
            response.status (201); // created
            return "Body was: " + body;
        });

        after ("/hi", (it, request, response) -> response.header ("after", "foobar"));

        try {
            Thread.sleep (500);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
