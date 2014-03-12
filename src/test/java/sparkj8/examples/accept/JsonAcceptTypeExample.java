package sparkj8.examples.accept;

import spark.Request;
import spark.Response;
import spark.Route;

import static spark.SparkJ8.get;

public class JsonAcceptTypeExample {

	public static void main(String args[]) {

		//Running curl -i -H "Accept: application/json" http://localhost:4567/hello json message is read.
		//Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is thrown.
		get("/hello", "application/json", (r, request, response) ->
            "{\"message\": \"Hello World\"}"
        );
	}

}
