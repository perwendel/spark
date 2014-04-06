package sparkj8c.servlet;

import static spark.SparkJ8C.after;
import static spark.SparkJ8C.before;
import static spark.SparkJ8C.get;
import static spark.SparkJ8C.post;

import spark.servlet.SparkApplication;

public class MyApp implements SparkApplication {

    @Override public void init () {
        System.out.println ("HELLO!!!");

        before ("/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/hi", it -> "Hello World!");

        get ("/:param", it -> "echo: " + it.params (":param"));

        get ("/", it -> "Hello Root!");

        post ("/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        after ("/hi", it -> it.header ("after", "foobar"));

        try {
            Thread.sleep (500);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
