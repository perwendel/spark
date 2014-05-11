package spark.examples.transformer;

import static spark.Spark.get;

public class TransformerExample {

    public static void main(String args[]) {
        get("/hello", "application/json", (request, response) -> {
            return new MyMessage("Hello World");
        }, new JsonTransformer());
    }

}
