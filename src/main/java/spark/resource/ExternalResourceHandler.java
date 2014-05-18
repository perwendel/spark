/*
 * Copyright 2014 - Per Wendel
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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.URIUtil;

import spark.utils.Assert;

/**
 * Locates resources in classpath
 * Created by Per Wendel on 2014-05-18.
 */
public class ExternalResourceHandler {

    private final String baseResource;
    private String welcomeFile;

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     */
    public ExternalResourceHandler(String baseResource) {
        this(baseResource, null);
    }

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     * @param welcomeFile  the welcomeFile
     */
    public ExternalResourceHandler(String baseResource, String welcomeFile) {
        Assert.notNull(baseResource);
        this.baseResource = baseResource;
        this.welcomeFile = welcomeFile;
    }

    /**
     * Gets a resource from a servlet request
     *
     * @param request the servlet request
     * @return the resource or null if not found
     * @throws MalformedURLException
     */
    public ExternalResource getResource(HttpServletRequest request) throws MalformedURLException {
        String servletPath;
        String pathInfo;
        Boolean included = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;

        if (included != null && included.booleanValue()) {
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

        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        return getResource(pathInContext);
    }

    /**
     * Gets resource
     * returns null if not exists
     */
    private ExternalResource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = URIUtil.canonicalPath(path);

            final String addedPath = addPaths(baseResource, path);

            ExternalResource resource = new ExternalResource(addedPath);

            if (resource.exists() && resource.isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ExternalResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (Exception e) {
            // Do nothing
        }
        return null;
    }

    /* ------------------------------------------------------------ */

    /**
     * Add two URI path segments.
     * Handles null and empty paths, path and query params (eg ?a=b or
     * ;JSESSIONID=xxx) and avoids duplicate '/'
     *
     * @param segment1 URI path segment (should be encoded)
     * @param segment2 URI path segment (should be encoded)
     * @return Legally combined path segments.
     */
    private static String addPaths(String segment1, String segment2) {
        if (segment1 == null || segment1.length() == 0) {
            if (segment1 != null && segment2 == null) {
                return segment1;
            }
            return segment2;
        }
        if (segment2 == null || segment2.length() == 0) {
            return segment1;
        }

        int split = segment1.indexOf(';');
        if (split < 0) {
            split = segment1.indexOf('?');
        }
        if (split == 0) {
            return segment2 + segment1;
        }
        if (split < 0) {
            split = segment1.length();
        }

        StringBuilder buf = new StringBuilder(segment1.length() + segment2.length() + 2);
        buf.append(segment1);

        if (buf.charAt(split - 1) == '/') {
            if (segment2.startsWith(URIUtil.SLASH)) {
                buf.deleteCharAt(split - 1);
                buf.insert(split - 1, segment2);
            } else {
                buf.insert(split, segment2);
            }
        } else {
            if (segment2.startsWith(URIUtil.SLASH)) {
                buf.insert(split, segment2);
            } else {
                buf.insert(split, '/');
                buf.insert(split + 1, segment2);
            }
        }

        return buf.toString();
    }

}
