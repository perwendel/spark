package spark.examples.transformer;

import static spark.Spark.get;
import spark.Request;
import spark.Response;

public class TransformerExample {

	public static void main(String args[]) {

		get(new JsonTransformer("/hello", "application/json") {
			@Override
			public Object handle(Request request, Response response) {
				return new MyMessage("Hello World");
			}
		});

	}
	
}
