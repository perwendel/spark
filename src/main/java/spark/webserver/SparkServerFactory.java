/*
 * Copyright 2011- Per Wendel
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
package spark.webserver;

import spark.resource.ClassPathResource;
import spark.resource.ExternalResource;
import spark.route.RouteMatcherFactory;
import spark.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Per Wendel
 */
public final class SparkServerFactory {

    private SparkServerFactory() {
    }

    public static SparkServer create(boolean hasMultipleHandler,
                                     String staticFileFolder,
                                     String externalStaticFileFolder)  {
        MatcherFilter matcherFilter = new MatcherFilter(RouteMatcherFactory.get(), false, hasMultipleHandler);
        matcherFilter.init(null);
        List<String> staticFileLocations = new ArrayList<>();
        List<String> externalFileLocations = new ArrayList<>();

        if(hasMultipleHandler)
        {
            ExternalResource externalResource = new ExternalResource(externalStaticFileFolder);
            ClassPathResource resource = new ClassPathResource(staticFileFolder);

            File externalLocations = null;
            File staticLocations = null;

            try {
                staticLocations = resource.getFile();
                externalLocations = externalResource.getFile();
            } catch (IOException e) {
                //ignore
            }
            if (staticLocations.listFiles() != null)
            {
                for(File file : staticLocations.listFiles())
                {
                    IOUtils.update(file, staticFileLocations,"");
                }
            }

            if (externalLocations.listFiles() != null)
            {
                for(File file : externalLocations.listFiles())
                {
                    IOUtils.update(file, externalFileLocations,"");
                }
            }
        }

        matcherFilter.addExternalLocations(externalFileLocations);
        matcherFilter.addStaticLocations(staticFileLocations);
        JettyHandler handler = new JettyHandler(matcherFilter);
        return new SparkServer(handler);
    }

}
