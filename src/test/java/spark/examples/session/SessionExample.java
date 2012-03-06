package spark.examples.session;

import static spark.Spark.*;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionExample {
    private static final String SESSION_NAME = "username";

    public static void main(String[] args) {
        get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                String name = request.session().attribute(SESSION_NAME);
                if (name == null) {
                    return "<html><body>What's your name?: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"name\"/><input type=\"submit\" value=\"go\"/></form></body></html>";
                } else {
                    return String.format("<html><body>Hello, %s!</body></html>", name);
                }
            }
        });

        post(new Route("/entry") {
            @Override
            public Object handle(Request request, Response response) {
                String name = request.queryParams("name");
                if (name != null) {
                    request.session().attribute(SESSION_NAME, name);
                }
                response.redirect("/");
                return null;
            }
        });
        
        get(new Route("/clear") {
            @Override
            public Object handle(Request request, Response response) {
                request.session().removeAttribute(SESSION_NAME);
                response.redirect("/");
                return null;
            }
        });
    }

}
