package spark.examples;

import static spark.Spark.get;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

abstract class FreeMarkerTemplateView extends TemplateViewRoute {

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

public class FreeMarkerExample {

    public static void main(String args[]) {

        get(new FreeMarkerTemplateView ("/hello") {
            @Override
            public ModelAndView handle(Request request, Response response) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("message", "Hello World");

                // The hello.ftl file is located in directory:
                // src/test/resources/spark/examples/templateview/freemarker
                return new ModelAndView(attributes, "hello.ftl");
            }
        });

    }
}
