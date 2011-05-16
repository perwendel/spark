package spark.test;

import spark.Route;
import spark.Spark;

public class SparkTest {
    
    public static void main(String[] args) {
        
        //  Spark.setPort(5678); <- Uncomment this if you wan't spark to listen on a port different than 4567.
        
        Spark.get(new Route("/hello") {
            @Override
            public Object handle() {
                return "Hello World!";
            }
        });
        
        Spark.get(new Route("/private") {
            @Override
            public Object handle() {
                status(401);
                return "Go Away!!!";
            }
            
        });
        
        Spark.get(new Route("/users/:name") {
            @Override
            public Object handle() {
                return "Selected user: " + params(":name");
            }
        });
        
        Spark.get(new Route("/news/:section") {
            @Override
            public Object handle() {
                type("text/xml");
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><news>" + params("section") + "</news>";
            }
        });
        
        Spark.get(new Route("/redirect") {
            @Override
            public Object handle() {
                redirect("/news/world");
                return null;
            }
        });
        
        Spark.get(new Route("/") {
            @Override
            public Object handle() {
                return "root";
            }
        });
        
        Spark.post(new Route("/") {
            @Override
            public Object handle() {
                return "echo: " + request.body();
            }
        });
    }
}
