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
package spark.resource;

import java.io.InputStream;
import java.net.MalformedURLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.utils.Assert;

import static spark.resource.AbstractResourceHandler.addPaths;

/**
 * Locates resources in jar file
 */
public class JarResourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JarResourceHandler.class);

    private final String baseResource;
    private String welcomeFile;

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     * @param welcomeFile  the welcomeFile
     */
    public JarResourceHandler(String baseResource, String welcomeFile) {
        Assert.notNull(baseResource);

        this.baseResource = baseResource;
        this.welcomeFile = welcomeFile;
    }

    public InputStream getResource(HttpServletRequest request) throws MalformedURLException {

        String servletPath;
        String pathInfo;

        boolean included = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;

        if (included) {
            servletPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String) request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);

            if (servletPath == null && pathInfo == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        } else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }

        String pathInContext = addPaths(servletPath, pathInfo);
        return getResourceStream(pathInContext);
    }

    private InputStream getResourceStream(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        InputStream resourceStream = null;

        try {
            path = UriPath.canonical(path);
            path = addPaths(baseResource, path);

            if (isDirectory(path)) {
                if (welcomeFileConfigured()) {
                    path = addPaths(path, welcomeFile);
                    resourceStream = loadStream(path);
                }
            } else {
                resourceStream = loadStream(path);
            }

        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getClass().getSimpleName() + " when trying to get resource. " + e.getMessage());
            }
        }

        return resourceStream;
    }

    private InputStream loadStream(String path) {
        return JarResourceHandler.class.getResourceAsStream(path);
    }

    private boolean welcomeFileConfigured() {
        return welcomeFile != null;
    }

    private boolean isDirectory(String path) {
        return path.endsWith("/");
    }

}
