package spark.examples.templateview;

import java.io.IOException;
import java.io.StringWriter;

import spark.ModelAndView;
import spark.TemplateViewRoute;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class FreeMarkerTemplateView extends TemplateViewRoute {

    private Configuration configuration;

    protected FreeMarkerTemplateView(String path) {
        super(path);
        this.configuration = createFreemarkerConfiguration();
    }

    protected FreeMarkerTemplateView(String path, String acceptType) {
        super(path, acceptType);
        this.configuration = createFreemarkerConfiguration();
    }

    @Override
    public String render(ModelAndView modelAndView) {
        try {
            StringWriter stringWriter = new StringWriter();

            Template template = configuration.getTemplate(modelAndView.getViewName());
            template.process(modelAndView.getModel(), stringWriter);

            return stringWriter.toString();

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(FreeMarkerTemplateView.class, "freemarker");
        return retVal;
    }

}
