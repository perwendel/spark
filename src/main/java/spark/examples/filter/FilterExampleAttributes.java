package spark.examples.filter;

import static spark.Spark.after;
import static spark.Spark.get;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class FilterExampleAttributes {

    public static void main(String[] args) {
        get(new Route("/hi") {
            @Override
            public Object handle(Request request, Response response) {
                request.attribute("foo", "bar");
                return null;
            }
        });
        
        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                for (String attr : request.attributes()) {
                    System.out.println("attr: " + attr);
                }
            }
        });
        
        after(new Filter("/hi") {
            @Override
            public void handle(Request request, Response response) {
                Object foo = request.attribute("foo");
                response.body(asXml("foo", foo));
            }
        });
    }
    
    private static String asXml(String name, Object value) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + name +">" + value + "</"+ name + ">";
    }
    
}
