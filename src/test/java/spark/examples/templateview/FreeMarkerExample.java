package spark.examples.templateview;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

public class FreeMarkerExample {

    public static void main(String args[]) {

        get(new FreeMarkerTemplateView("/hello") {
            @Override
            public ModelAndView handle(Request request, Response response) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("message", "Hello World");
                return new ModelAndView(attributes, "hello.ftl");
            }
        });

    }

}