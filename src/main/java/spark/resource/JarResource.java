/*
 * Copyright 2016
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        return JarResourceHandler.class.getResourceAsStream(path);
    }

    @Override
    public String getFilename() {
        return path.substring(1 + path.lastIndexOf("/"));
    }

    @Override
    public boolean isReadable() {
        
        int read = -1;
        try(InputStream is = JarResourceHandler.class.getResourceAsStream(path)) {
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
