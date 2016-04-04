package spark.examples.hello;

import static spark.Spark.get;
import static spark.Spark.http2;
import static spark.Spark.port;
import static spark.Spark.secure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureHttp2World {
    public static void main(String[] args) {
        secure(args[0], args[1], null, null);
        http2();
        get("/hello", (request, response) -> {
            return "Hello Http 2 Secure World!";
        });

    }
}
