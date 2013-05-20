package spark;

public abstract class ResponseTransformerRoute extends Route {

	protected ResponseTransformerRoute(String path) {
		super(path);
	}

	protected ResponseTransformerRoute(String path, String acceptType) {
		super(path, acceptType);
	}

	public abstract Model handle(Request request, Response response);

	@Override
	public String render(Object object) {
		Model model = (Model)object;
		return render(model);
	}
	
	public abstract String render(Model model);
	
}
