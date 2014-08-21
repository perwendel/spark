package spark.examples.hello;

import static spark.Spark.get;
import static spark.Spark.secure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureWorld {
    public static void main(String[] args) {

        secure(args[0], args[1], null, null);
        get("/hello", (request, response) -> {
            return "Hello Secure World!";
        });

    }
}
