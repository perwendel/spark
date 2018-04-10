package spark.examples.transformer;

import static spark.Spark.get;
import static spark.Spark.setDefaultResponseTransformer;

public class DefaultTransformerExample {

    public static void main(String args[]) {

        setDefaultResponseTransformer(new JsonTransformer());

        get("/hello", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        });

        get("/hello2", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        }, model -> "custom transformer");
    }

}
