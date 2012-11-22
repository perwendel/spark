package spark;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;

public class SessionTest {

    public static void main(String[] args) {
        before(new Filter("/*") {
            @Override
            public void handle(Request request, Response response) {
                if (!request.url().endsWith("/login")) {
                    User user = (User) request.session().get("user");
                    if (user == null) {
                        response.redirect("/login");
                    }
                }
            }
        });

        get(new Route("/home") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><body>Home</body></html>";
            }
        });

        get(new Route("/login") {
            @Override
            public Object handle(Request request, Response response) {
                return "<html><body>Login Form<form action='/login' method='post'><input type='submit'/></form></body></html>";
            }
        });

        post(new Route("/login") {
            @Override
            public Object handle(Request request, Response response) {
                request.session().set("user", new User(1, "user", "John Joe"));
                response.redirect("/home");
                return null;
            }
        });
    }

    static class User implements Serializable {

        int userId;
        String username;
        String fullName;

        User(int userId, String username, String fullName) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getFullName() {
            return fullName;
        }
    }
}
