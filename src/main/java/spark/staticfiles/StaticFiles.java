/*
 * Copyright 2015 - Per Wendel
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
package spark.staticfiles;

import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import spark.utils.Assert;

/**
 * Utility methods for setting internal and external static file locations.
 */
public class StaticFiles {

    /**
     * Adds static file location to 'handlersInList' if present
     *
     * @param staticFileLocation staticFileLocation
     * @param handlersInList     handlersInList
     */
    public static void setLocationIfPresent(String staticFileLocation,
                                            List<Handler> handlersInList) {

        Assert.notNull(handlersInList, "'handlersInList' must not be null");

        if (staticFileLocation != null) {
            ResourceHandler resourceHandler = new ResourceHandler();
            Resource staticResources = Resource.newClassPathResource(staticFileLocation);
            resourceHandler.setBaseResource(staticResources);
            resourceHandler.setWelcomeFiles(new String[] {"index.html"});
            handlersInList.add(resourceHandler);
        }
    }

    /**
     * Adds external static file location to 'handlersInList' if present
     *
     * @param externalFileLocation externalFileLocation
     * @param handlersInList       handlersInList
     */
    public static void setExternalLocationIfPresent(String externalFileLocation,
                                                    List<Handler> handlersInList) {

        Assert.notNull(handlersInList, "'handlersInList' must not be null");

        if (externalFileLocation != null) {
            ResourceHandler externalResourceHandler = new ResourceHandler();
            externalResourceHandler.setResourceBase(externalFileLocation);
            externalResourceHandler.setWelcomeFiles(new String[] {"index.html"});
            handlersInList.add(externalResourceHandler);
        }
    }

}
