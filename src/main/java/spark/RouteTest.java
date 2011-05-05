package spark;

public class RouteTest {

    @Route("get '/hi'")
    public static String hiResource() {
        return "Hello World!";
    }
    
    @Route("get '/new'")
    public static String newRes() {
        return "new resource";
    }
    
    @Route("get '/xml'")
    public String zml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag><sub>Hello World Spark!</sub></tag>";
    }
    
    @Route("get '/bbb/:p2'")
    public static String params() {
        return "Params response";
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
