package spark.examples.transformer;

import spark.ResponseTransformer;

import static spark.Spark.get;
import static spark.Spark.defaultResponseTransformer;

public class DefaultTransformerExample {

    public static void main(String args[]) {

        defaultResponseTransformer(json);

        get("/hello", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        });

        get("/hello2", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        }, model -> "custom transformer");
    }

    private static final ResponseTransformer json = new JsonTransformer();

}
