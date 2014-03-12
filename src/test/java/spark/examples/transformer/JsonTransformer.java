package spark.examples.transformer;

import com.google.gson.Gson;
import spark.ResponseTransformerRoute;

public abstract class JsonTransformer extends ResponseTransformerRoute {

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
