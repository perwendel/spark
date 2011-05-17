package spark.examples.hello;

import spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        Spark.get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });
    }
}