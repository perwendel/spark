package spark.examples.transformer;

import spark.ResponseTransformer;

import com.google.gson.Gson;

public class JsonTransformer implements ResponseTransformer {

	private Gson gson = new Gson();

	@Override
	public String render(Object model) {
		return gson.toJson(model);
	}

}
