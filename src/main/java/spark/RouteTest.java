package spark;

public class RouteTest {

    @Route("get '/hi'")
    public static String hiResource() {
        return "Hello World!";
    }
    
    @Route("get '/:p1/:p2'")
    public static String newRes(WebContext context) {
        return "p1 = " + context.getParam("p1") + ", p2 = " + context.getParam("p2");
    }
    
    @Route("get '/xml'")
    public static String zml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag><sub>Hello World Spark!</sub></tag>";
    }
    
    @Route("get '/bbb/:p2'")
    public static String params(WebContext context) {
        return "Params response: " + context.getParam("p2");
    }
    
    @Route("get '/html'")
    public static String html() {
        return "<html><head><title>Spark</title></head><body><div>Yalla bruschan</div></body></html>";
    }
    
//    private static class WebContext {
//        private Object params;
//        private HttpServletRequest httpServletRequest;
//        private HttpServletResponse httpServletResponse;
//        
//    }
    
}
