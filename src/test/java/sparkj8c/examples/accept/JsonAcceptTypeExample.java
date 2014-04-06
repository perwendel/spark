package sparkj8c.examples.accept;

import static spark.SparkJ8C.get;

public class JsonAcceptTypeExample {
	public static void main(String args[]) {
		/*
		 * Running curl -i -H "Accept: application/json" http://localhost:4567/hello json message
		 * is read.
		 * Running curl -i -H "Accept: text/html" http://localhost:4567/hello HTTP 404 error is
		 * thrown.
		 */
        get("/hello", "application/json", it -> "{\"message\": \"Hello World\"}");
	}
}
