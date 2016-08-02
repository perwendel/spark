package spark.staticfiles;

import spark.resource.AbstractFileResolvingResource;
import spark.utils.GzipUtils;
import spark.utils.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


class ResourceResolver {
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private AbstractFileResolvingResource resource;
    private Map<String, String> customHeaders;

    public ResourceResolver(HttpServletRequest httpRequest,
                            HttpServletResponse httpResponse, AbstractFileResolvingResource resource,
                            Map<String, String> customHeaders) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.resource = resource;
        this.customHeaders = customHeaders;
    }

    /**
        Override this if you need to intercept/replace/redirect static resources.
        Maybe you only need to do this for testing
     */
    public boolean invoke() throws IOException {
        httpResponse.setHeader(MimeType.CONTENT_TYPE, MimeType.fromResource(resource));
        customHeaders.forEach(httpResponse::setHeader); //add all user-defined headers to response
        OutputStream wrappedOutputStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, false);
        IOUtils.copy(resource.getInputStream(), wrappedOutputStream);
        wrappedOutputStream.flush();
        wrappedOutputStream.close();
        return true;
    }

    public interface Factory {
        ResourceResolver create(HttpServletRequest httpRequest,
                                HttpServletResponse httpResponse, AbstractFileResolvingResource resource,
                                Map<String, String> customHeaders);

        public class Default implements Factory {
            @Override
            public ResourceResolver create(HttpServletRequest httpRequest, HttpServletResponse httpResponse, AbstractFileResolvingResource resource, Map<String, String> customHeaders) {
                return new ResourceResolver(httpRequest, httpResponse, resource, customHeaders);
            }
        }
    }

}
