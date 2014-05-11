package spark;


/**
 * A Template holds the implementation of the 'render' method.
 * TemplateViewRoute instead of returning the result of calling toString() as body, it returns the result of calling render method.
 * The primary purpose of this kind of Route is provide a way to create generic and reusable components for rendering output using a Template Engine. For example to render objects to html by using Freemarker template engine..
 *
 * @author alex
 */
public abstract class TemplateEngine {

    /**
     * Renders the object
     *
     * @param object the object
     * @return the rendered model and view
     */
    public String render(Object object) {
        ModelAndView modelAndView = (ModelAndView) object;
        return render(modelAndView);
    }

    /**
     * Creates a new ModelAndView object with given arguments.
     *
     * @param model    object.
     * @param viewName to be rendered.
     * @return object with model and view set.
     */
    public ModelAndView modelAndView(Object model, String viewName) {
        return new ModelAndView(model, viewName);
    }

    /**
     * Method called to render the output that is sent to client.
     *
     * @param modelAndView object where object (mostly a POJO) and the name of the view to render are set.
     * @return message that it is sent to client.
     */
    public abstract String render(ModelAndView modelAndView);

}
