package spark.examples.transformer;

import spark.ResponseTransformerRoute;

import com.google.gson.Gson;

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
