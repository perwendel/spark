package spark.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tommaso Doninelli
 */
public class JarResource extends AbstractFileResolvingResource {

    private static final Logger LOG = LoggerFactory.getLogger(JarResource.class);
    private final String path;
    
    public JarResource(String path){
        this.path = path;
    }
    
    @Override
    public String getDescription() {
        return "external jar resource [" + path + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return JarResourceHandler2.class.getResourceAsStream(path);
    }

    @Override
    public String getFilename() {
        return path.substring(1 + path.lastIndexOf("/"));
    }

    @Override
    public boolean isReadable() {
        
        int read = -1;
        try(InputStream is = JarResourceHandler2.class.getResourceAsStream(path)) {
            read = is.read();
        }
        catch (Exception e) {
            LOG.error("Could not read resource " + path);
        }                
        return read > 0;
    }

    @Override
    public long lastModified() throws IOException {
        URL url = JarResource.class.getResource(path);
        return url.openConnection().getLastModified();        
    }

    @Override
    public URL getURL() throws IOException {
        return JarResource.class.getResource(path);
    }

    @Override
    public boolean exists() {
        return isReadable();
    }
        
}
