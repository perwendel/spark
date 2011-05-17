/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark.examples.simple;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * A simple example just showing some basic functionality
 *
 * @author Per Wendel
 */
public class SimpleExample {
    
    public static void main(String[] args) {
        
        //  Spark.setPort(5678); <- Uncomment this if you wan't spark to listen on a port different than 4567.
        
        Spark.get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World!";
            }
        });
        
        Spark.post(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello World: " + request.body();
            }
        });
        
        Spark.get(new Route("/private") {
            @Override
            public Object handle(Request request, Response response) {
                response.status(401);
                return "Go Away!!!";
            }
            
        });
        
        Spark.get(new Route("/users/:name") {
            @Override
            public Object handle(Request request, Response response) {
                return "Selected user: " + request.params(":name");
            }
        });
        
        Spark.get(new Route("/news/:section") {
            @Override
            public Object handle(Request request, Response response) {
                response.type("text/xml");
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + request.params("section") + "</news>";
            }
        });
        
        Spark.get(new Route("/redirect") {
            @Override
            public Object handle(Request request, Response response) {
                response.redirect("/news/world");
                return null;
            }
        });
        
        Spark.get(new Route("/") {
            @Override
            public Object handle(Request request, Response response) {
                return "root";
            }
        });
        
    }
}
