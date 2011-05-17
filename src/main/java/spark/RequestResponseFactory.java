package spark;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spark.route.RouteMatch;

public class RequestResponseFactory {

    public static Request create(RouteMatch match, HttpServletRequest request) {
        return new Request(match, request);
    }
    
    public static Response create(HttpServletResponse response) {
        return new Response(response);
    }
    
}
