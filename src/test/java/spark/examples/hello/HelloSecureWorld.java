package spark.examples.hello;

import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.get;
import static spark.Spark.setSecure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureWorld {
    public static void main(String[] args) {

        setSecure(args[0], args[1], null, null);
        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello Secure World!";
            }
        });

    }
}
