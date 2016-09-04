package spark.examples.session;

import static spark.Spark.get;
import static spark.Spark.post;

public class SessionExample {
    private static final String SESSION_NAME = "username";

    public static void main(String[] args) {
        get("/", (request, response) -> {
            String name = request.session().attribute(SESSION_NAME);
            if (name == null) {
                return "<html><body>What's your name?: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"name\"/><input type=\"submit\" value=\"go\"/></form></body></html>";
            } else {
                return String.format("<html><body>Hello, %s!</body></html>", name);
            }
        });

        post("/entry", (request, response) -> {
            String name = request.queryParams("name");
            if (name != null) {
                request.session().attribute(SESSION_NAME, name);
            }
            response.redirect("/");
            return null;
        });

        get("/clear", (request, response) -> {
            request.session().removeAttribute(SESSION_NAME);
            response.redirect("/");
            return null;
        });
    }

}
