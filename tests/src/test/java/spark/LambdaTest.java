package spark;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;

/**
 * Created by Per Wendel on 2014-05-10.
 */
public class LambdaTest {

    public static void main(String[] args) {
        get("/hello", (request, response) -> {
            System.out.println("request = " + request.pathInfo());
            return "Hello World";
        });

        before("/protected/*", "application/xml", (request, response) -> {
            halt(401, "<xml>fuck off</xml>");
        });

        before("/protected/*", "application/json", (request, response) -> {
            halt(401, "{\"message\": \"Go Away!\"}");
        });

    }

}
