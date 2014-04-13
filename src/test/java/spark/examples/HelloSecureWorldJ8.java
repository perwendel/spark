package spark.examples;

import static spark.SparkJ8.get;
import static spark.SparkJ8.setSecure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 */
public class HelloSecureWorldJ8 {
    public static void main(String[] args) {
        setSecure(args[0], args[1], null, null);
        get("/hello", it -> "Hello Secure World!");
    }
}
