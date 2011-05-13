/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark;

public class RouteTest {

    @Route("get '/hi'")
    public static String hiResource() {
        return "Hello World!";
    }
    
    @Route("hello '/:p1/:p2'")
    public static String newRest(WebContext context) {
        return "p1 = " + context.getParam("p1") + ", p2 = " + context.getParam("p2");
    }
    
    @Route("get '/:p1/:p2'")
    public static String newRes(WebContext context) {
        System.out.println("path info: " + context.getPathInfo());
        return "p1 = " + context.getParam("p1") + ", p2 = " + context.getParam("p2");
    }
    
    @Route("get '/xml'")
    public static String zml(WebContext context) {
        String query = "";
        for (String queryParam : context.getQueryParams()) {
            query += "," + queryParam;
        }
        System.out.println(context.getScheme() + " on port: " + context.getPort() + ", user-agent: " + context.getUserAgent());
        System.out.println(context.getClientIP());
        System.out.println(context.getHost());
        System.out.println(context.getUrl());
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag><sub>Hello World Spark! " + query + "</sub></tag>";
    }
    
    @Route("post '/xml'")
    public static String postXml(WebContext context) {
        for (String header : context.getHeaders()) {
            System.out.println(header + " = " + context.getHeader(header));
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag><sub>echo: " + context.getBody() + ", cl = " + context.getContentLength() + "</sub></tag>";
    }
    
    @Route("get '/bbb/:p2'")
    public static void params(WebContext context) {
        context.redirect("/xml/gris");
//        return null;
        //return "Params response: " + context.getParam("p2");
    }
    
    @Route("get '/html'")
    public static String html(WebContext context) {
        String id = context.getQueryParam("id");
        return "<html><head><title>Spark</title></head><body><div>Yalla bruschan: " + id + "</div></body></html>";
    }
    
//    private static class WebContext {
//        private Object params;
//        private HttpServletRequest httpServletRequest;
//        private HttpServletResponse httpServletResponse;
//        
//    }
    
}
