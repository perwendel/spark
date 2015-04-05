//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//
package spark.resource;

import java.net.MalformedURLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.URIUtil;

/**
 * Abstract class providing functionality for finding resources based on an Http Servlet request.
 * Code snippets copied from Eclipse Jetty source. Modifications made by Per Wendel.
 */
public abstract class AbstractResourceHandler {

    /**
     * Gets a resource from a servlet request
     *
     * @param request the servlet request
     * @return the resource or null if not found
     * @throws java.net.MalformedURLException thrown when malformed URL.
     */
    public AbstractFileResolvingResource getResource(HttpServletRequest request) throws MalformedURLException {
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

        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        return getResource(pathInContext);
    }

    /**
     * Gets resource from path
     *
     * @param path the path
     * @return the resource or null if resource doesn't exist
     * @throws java.net.MalformedURLException thrown when malformed URL.
     */
    protected abstract AbstractFileResolvingResource getResource(String path) throws MalformedURLException;

    /**
     * Add two URI path segments.
     * Handles null and empty paths, path and query params (eg ?a=b or
     * ;JSESSIONID=xxx) and avoids duplicate '/'
     *
     * @param segment1 URI path segment (should be encoded)
     * @param segment2 URI path segment (should be encoded)
     * @return Legally combined path segments.
     */
    protected static String addPaths(String segment1, String segment2) {
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
