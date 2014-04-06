package sparkj8c.examples.hello;

import static spark.SparkJ8C.get;
import static spark.SparkJ8C.setSecure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureWorld {
    public static void main(String[] args) {
        setSecure(args[0], args[1], null, null);
        get("/hello", it -> "Hello Secure World!");
    }
}
