package spark.servlet;

import static spark.SparkJ8.after;
import static spark.SparkJ8.before;
import static spark.SparkJ8.get;
import static spark.SparkJ8.post;

public class MyAppJ8 implements SparkApplication {

    @Override public void init () {
        System.out.println ("HELLO J8!!!");

        before ("/j8/protected/*", it -> it.halt (401, "Go Away!"));

        get ("/j8/hi", it -> "Hello World!");

        get ("/j8/:param", it -> "echo: " + it.params (":param"));

        get ("/j8/", it -> "Hello Root!");

        post ("/j8/poster", it -> {
            String body = it.requestBody ();
            it.status (201); // created
            return "Body was: " + body;
        });

        after ("/j8/hi", it -> it.header ("after", "foobar"));

        try {
            Thread.sleep (500);
        }
        catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
