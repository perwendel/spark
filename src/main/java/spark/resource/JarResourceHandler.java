package spark.resource;

import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tommaso Doninelli
 */
public class JarResourceHandler  extends AbstractResourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JarResourceHandler.class);

    private final String baseResource;
    private final String welcomeFile;
    
    public JarResourceHandler(String baseResource, String welcomeFile) {
        this.baseResource = baseResource;
        this.welcomeFile = welcomeFile;
    }

    @Override
    protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
        if(path.equals("/")) {
            path += welcomeFile;
        }
        return new JarResource(addPaths(baseResource, path));
    }

}
