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
import java.io.InputStream;
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
import spark.resource.ClassPathResource;
import spark.resource.ClassPathResourceHandler;
import spark.resource.ExternalResource;
import spark.resource.ExternalResourceHandler;
import spark.resource.JarResourceHandler;
import spark.utils.Assert;
import spark.utils.GzipUtils;
import spark.utils.IOUtils;

/**
 * Holds the static file configuration.
 * TODO: ETAG ?
 */
public class StaticFilesConfiguration {
    private final Logger LOG = LoggerFactory.getLogger(StaticFilesConfiguration.class);

    private List<AbstractResourceHandler> staticResourceHandlers = null;
    private List<JarResourceHandler> jarResourceHandlers = null;

    private boolean staticResourcesSet = false;
    private boolean externalStaticResourcesSet = false;

    public static StaticFilesConfiguration servletInstance = new StaticFilesConfiguration();

    private Map<String, String> customHeaders = new HashMap<>();

    /**
     * @return true if consumed, false otherwise.
     */
    public boolean consume(HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) throws IOException {

        if (consumeWithFileResourceHandlers(httpRequest, httpResponse)) {
            return true;
        }

        if (consumeWithJarResourceHandler(httpRequest, httpResponse)) {
            return true;
        }

        return false;
    }


    private boolean consumeWithFileResourceHandlers(HttpServletRequest httpRequest,
                                                    HttpServletResponse httpResponse) throws IOException {
        if (staticResourceHandlers != null) {

            for (AbstractResourceHandler staticResourceHandler : staticResourceHandlers) {

                AbstractFileResolvingResource resource = staticResourceHandler.getResource(httpRequest);

                if (resource != null && resource.isReadable()) {
                    httpResponse.setHeader(MimeType.CONTENT_TYPE, MimeType.fromResource(resource));
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

    private boolean consumeWithJarResourceHandler(HttpServletRequest httpRequest,
                                                  HttpServletResponse httpResponse) throws IOException {
        if (jarResourceHandlers != null) {

            for (JarResourceHandler jarResourceHandler : jarResourceHandlers) {
                InputStream stream = jarResourceHandler.getResource(httpRequest);

                if (stream != null) {
                    OutputStream wrappedOutputStream = GzipUtils.checkAndWrap(httpRequest, httpResponse, false);
                    customHeaders.forEach(httpResponse::setHeader); //add all user-defined headers to response

                    IOUtils.copy(stream, wrappedOutputStream);

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

        if (staticResourceHandlers != null) {
            staticResourceHandlers.clear();
            staticResourceHandlers = null;
        }

        if (jarResourceHandlers != null) {
            jarResourceHandlers.clear();
            jarResourceHandlers = null;
        }

        staticResourcesSet = false;
        externalStaticResourcesSet = false;
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized void configure(String folder) {
        Assert.notNull(folder, "'folder' must not be null");

        if (!staticResourcesSet) {
            try {
                ClassPathResource resource = new ClassPathResource(folder);

                if (configureJarCase(folder, resource)) {
                    return;
                }

                if (!resource.getFile().isDirectory()) {
                    LOG.error("Static resource location must be a folder");
                    return;
                }

                if (staticResourceHandlers == null) {
                    staticResourceHandlers = new ArrayList<>();
                }

                staticResourceHandlers.add(new ClassPathResourceHandler(folder, "index.html"));
                LOG.info("StaticResourceHandler configured with folder = " + folder);
            } catch (IOException e) {
                LOG.error("Error when creating StaticResourceHandler", e);
            }
            staticResourcesSet = true;
        }

    }

    private boolean configureJarCase(String folder, ClassPathResource resource) throws IOException {
        if (resource.getURL().getProtocol().equals("jar")) {

            InputStream stream = StaticFilesConfiguration.class.getResourceAsStream(folder);

            if (stream != null) {
                if (jarResourceHandlers == null) {
                    jarResourceHandlers = new ArrayList<>();
                }

                // Add jar file resource handler
                jarResourceHandlers.add(new JarResourceHandler(folder, "index.html"));
                staticResourcesSet = true;
                return true;
            } else {
                LOG.error("Static file configuration failed.");
            }

        }
        return false;
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public synchronized void configureExternal(String folder) {
        Assert.notNull(folder, "'folder' must not be null");

        if (!externalStaticResourcesSet) {
            try {
                ExternalResource resource = new ExternalResource(folder);
                if (!resource.getFile().isDirectory()) {
                    LOG.error("External Static resource location must be a folder");
                    return;
                }

                if (staticResourceHandlers == null) {
                    staticResourceHandlers = new ArrayList<>();
                }
                staticResourceHandlers.add(new ExternalResourceHandler(folder, "index.html"));
                LOG.info("External StaticResourceHandler configured with folder = " + folder);
            } catch (IOException e) {
                LOG.error("Error when creating external StaticResourceHandler", e);
            }
            externalStaticResourcesSet = true;
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
