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
