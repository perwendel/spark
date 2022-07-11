package spark.examples.hello;

import static spark.Spark.get;
import static spark.Spark.http2;
import static spark.Spark.secure;

/**
 * You'll need to provide a JKS keystore as arg 0 and its password as arg 1.
 * And also to add a ALPN server implementation to your maven/gradle config:
 * jetty-alpn-openjdk8-server for JDK 8
 * jetty-alpn-java-server for JDK >= 9
 * jetty-alpn-conscrypt-server for native SSL implementation (JDK >= 8)
 * If using Conscrypt you will also need to add the following line before
 * enabling http2: "Security.insertProviderAt(new OpenSSLProvider(), 1);"
 * Docs for the ALPN are available at:
 * https://www.eclipse.org/jetty/documentation/current/alpn-chapter.html
 */
public class HelloSecureHttp2World {
    public static void main(String[] args) {
        secure(args[0], args[1], null, null);
        http2();
        get("/hello", (request, response) -> {
            return "Hello Secure World!";
        });
    }
}
