package spark;

import spark.resource.AbstractFileResolvingResource;
import spark.resource.ExternalResourceHandler;
import spark.utils.IOUtils;

import java.io.StringWriter;
import java.net.MalformedURLException;

public class Alias {
    static Alias create(Routable http) {
        return new Alias(http);
    }

    private Alias(Routable http) {
        this.http = http;
    }

    private Routable http;


    /**
     * Redirects any HTTP request of type GET on 'fromPath' to 'toPath'
     *
     * @param fromPath from path
     * @param toPath   to path
     */
    public void get(String fromPath, String toPath, String location) {
        get(fromPath, toPath, location,null);
    }


    /**
     * Redirects any HTTP request of type GET on 'fromPath' to 'toPath' with the provided redirect 'status' code.
     *
     * @param fromPath from path
     * @param toPath   to path
     * @param mimeType   mimeType
     */
    public void get(String fromPath, String toPath, String location,String mimeType) {
        http.get(fromPath, (request, response)->{
            ExternalResourceHandler erh = new ExternalResourceHandler("src/main/resources/"+location) {
                @Override
                protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
                    return super.getResource(toPath);
                }
            };
            if(mimeType!=null)
                response.type(mimeType);
            final StringWriter writer = new StringWriter();
            IOUtils.copy(erh.getResource(request.raw()).getInputStream(), writer);
            return writer.toString();
        });
    }

}
