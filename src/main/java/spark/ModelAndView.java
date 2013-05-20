package spark;

public class ModelAndView {

	private Object model;
	private String viewName;
	
	public ModelAndView(Object model, String viewName) {
		super();
		this.model = model;
		this.viewName = viewName;
	}
	
	public ModelAndView(Model model, String viewName) {
		super();
		this.model = model.getModel();
		this.viewName = viewName;
	}
	
	public Object getModel() {
		return model;
	}
	
	public String getViewName() {
		return viewName;
	}
	
}
