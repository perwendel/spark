package spark;

public abstract class TemplateViewRoute extends Route {

	protected TemplateViewRoute(String path) {
		super(path);
	}

	protected TemplateViewRoute(String path, String acceptType) {
		super(path, acceptType);
	}

	public abstract ModelAndView handle(Request request, Response response);
	
	@Override
	public String render(Object object) {
		ModelAndView modelAndView = (ModelAndView)object;
		return render(modelAndView);
	}
	
	public abstract String render(ModelAndView modelAndView);
	
}
