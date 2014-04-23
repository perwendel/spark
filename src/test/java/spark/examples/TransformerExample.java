package spark.examples;

import static spark.Spark.get;

import spark.Request;
import spark.Response;

import com.google.gson.Gson;
import spark.ResponseTransformerRoute;

abstract class JsonTransformer extends ResponseTransformerRoute {

    private Gson gson = new Gson();

    protected JsonTransformer(String path) {
        super(path);
    }

    protected JsonTransformer(String path, String acceptType) {
        super(path, acceptType);
    }

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

}

class MyMessage {

	private String message;

	public MyMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
public class TransformerExample {

	public static void main(String args[]) {

		get(new JsonTransformer ("/hello", "application/json") {
			@Override
			public Object handle(Request request, Response response) {
				return new MyMessage("Hello World");
			}
		});

	}
}
