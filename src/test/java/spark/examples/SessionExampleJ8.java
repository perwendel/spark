package spark.examples;

import static spark.SparkJ8.get;
import static spark.SparkJ8.post;

public class SessionExampleJ8 {
    private static final String SESSION_NAME = "username";

    public static void main (String[] args) {
        get ("/", it -> {
            String name = it.session ().attribute (SESSION_NAME);
            if (name == null) {
                return
                    "<html>" +
                    "    <body>" +
                    "        What's your name?:" +
                    "        <form action=\"/entry\" method=\"POST\">" +
                    "            <input type=\"text\" name=\"name\"/>" +
                    "            <input type=\"submit\" value=\"go\"/>" +
                    "        </form>" +
                    "    </body>" +
                    "</html>";
            }
            else {
                return String.format ("<html><body>Hello, %s!</body></html>", name);
            }
        });

        post ("/entry", it -> {
            String name = it.queryParams ("name");
            if (name != null) {
                it.session ().attribute (SESSION_NAME, name);
            }
            it.redirect ("/");
            return null;
        });

        get ("/clear", it -> {
            it.session ().removeAttribute (SESSION_NAME);
            it.redirect ("/");
            return null;
        });
    }
}
