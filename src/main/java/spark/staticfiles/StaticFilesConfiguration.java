/*
 * Copyright 2016 - Per Wendel
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.resource.AbstractFileResolvingResource;
import spark.resource.AbstractResourceHandler;
import spark.resource.ClassPathResourceHandler;
import spark.resource.ExternalResource;
import spark.resource.ExternalResourceHandler;
import spark.utils.Assert;
import spark.utils.GzipUtils;
import spark.utils.IOUtils;

/**
 * Holds the static file configuration.
 * TODO: ETAG ?
 */
public class StaticFilesConfiguration {
    private final Logger LOG = LoggerFactory.getLogger(StaticFilesConfiguration.class);

    private Map<String, AbstractResourceHandler> staticResourceHandlers = new HashMap<>();


    public static StaticFilesConfiguration servletInstance = new StaticFilesConfiguration();

    private Map<String, String> customHeaders = new HashMap<>();

    /**
     * Attempt consuming using either static resource handlers or jar resource handlers
     *
     * @param httpRequest  The HTTP servlet request.
     * @param httpResponse The HTTP servlet response.
     * @return true if consumed, false otherwise.
     * @throws IOException in case of IO error.
     */
    public boolean consume(HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) throws IOException {
        try {
            if (consumeWithFileResourceHandlers(httpRequest, httpResponse)) {
                return true;
            }

        } catch (DirectoryTraversal.DirectoryTraversalDetection directoryTraversalDetection) {
            LOG.warn(directoryTraversalDetection.getMessage() + " directory traversal detection for path: "
                             + httpRequest.getPathInfo());
        }
        return false;
    }


    private boolean consumeWithFileResourceHandlers(HttpServletRequest httpRequest,
                                                    HttpServletResponse httpResponse) throws IOException {
        if (staticResourceHandlers != null) {

            for (AbstractResourceHandler staticResourceHandler : staticResourceHandlers.values()) {

                AbstractFileResolvingResource resource = staticResourceHandler.getResource(httpRequest);

                if (resource != null && resource.isReadable()) {

                    if (MimeType.shouldGuess()) {
                        httpResponse.setHeader(MimeType.CONTENT_TYPE, MimeType.fromResource(resource));
                    }
                    customHeaders.forEach(httpResponse::setHeader); //add all user-defined headers to response
                    OutputStream wrappedOutputStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, false);

                    IOUtils.copy(resource.getInputStream(), wrappedOutputStream);
                    wrappedOutputStream.flush();
                    wrappedOutputStream.close();
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Clears all static file configuration
     */
    public void clear() {
        staticResourceHandlers.clear();
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized void add(String folder) {
        Assert.notNull(folder, "'folder' must not be null");

       if(staticResourceHandlers.containsKey(folder)){
           LOG.warn("Static resource location ignored(already add)" + folder);
           return;
       }


        staticResourceHandlers.put(folder, new ClassPathResourceHandler(folder, "index.html"));
        LOG.info("StaticResourceHandler configured with folder = " + folder);
        //StaticFilesFolder.localConfiguredTo(folder);

    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized void configureExternal(String folder) {
        Assert.notNull(folder, "'folder' must not be null");

        try {
            ExternalResource resource = new ExternalResource(folder);
            if (!resource.getFile().isDirectory()) {
                LOG.error("External Static resource location must be a folder");
                return;
            }

            if(staticResourceHandlers.containsKey(folder))
            {
                LOG.warn("External Static resource location ignored(already add)" + folder);
                return;
            }

            staticResourceHandlers.put(folder, new ExternalResourceHandler(folder, "index.html"));
            LOG.info("External StaticResourceHandler configured with folder = " + folder);
        } catch (IOException e) {
            LOG.error("Error when creating external StaticResourceHandler", e);
        }
    }

    public static StaticFilesConfiguration create() {
        return new StaticFilesConfiguration();
    }

    public void setExpireTimeSeconds(long expireTimeSeconds) {
        customHeaders.put("Cache-Control", "private, max-age=" + expireTimeSeconds);
        customHeaders.put("Expires", new Date(System.currentTimeMillis() + (expireTimeSeconds * 1000)).toString());
    }

    public void putCustomHeaders(Map<String, String> headers) {
        customHeaders.putAll(headers);
    }

    public void putCustomHeader(String key, String value) {
        customHeaders.put(key, value);
    }
}
