package spark.examples.accept;

import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;

public class JsonAcceptTypeExample {

	public static void main(String args[]) {

		//Running curl -i -H "Accept: application/json" http://localhost:4567/hello json message is read.
		//Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is thrown.
		get(new Route("/hello", "application/json") {
			@Override
			public Object handle(Request request, Response response) {
				return "{\"message\": \"Hello World\"}";
			}
		});

	}

}
