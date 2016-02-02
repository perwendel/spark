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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.utils.Assert;

/**
 * Locates resources in classpath
 * Code snippets copied from Eclipse Jetty source. Modifications made by Per Wendel.
 */
public class ClassPathResourceHandler extends AbstractResourceHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathResourceHandler.class);

    private final String baseResource;
    private String welcomeFile;

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     */
    public ClassPathResourceHandler(String baseResource) {
        this(baseResource, null);
    }

    /**
     * Constructor
     *
     * @param baseResource the base resource path
     * @param welcomeFile  the welcomeFile
     */
    public ClassPathResourceHandler(String baseResource, String welcomeFile) {
        Assert.notNull(baseResource);

        this.baseResource = baseResource;
        this.welcomeFile = welcomeFile;
    }

    @Override
    protected AbstractFileResolvingResource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        try {
            path = canonicalPath(path);

            final String addedPath = addPaths(baseResource, path);

            ClassPathResource resource = new ClassPathResource(addedPath);

            if (resource.exists() && resource.getFile().isDirectory()) {
                if (welcomeFile != null) {
                    resource = new ClassPathResource(addPaths(resource.getPath(), welcomeFile));
                } else {
                    //  No welcome file configured, serve nothing since it's a directory
                    resource = null;
                }
            }

            return (resource != null && resource.exists()) ? resource : null;
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getClass().getSimpleName() + " when trying to get resource. " + e.getMessage());
            }
        }
        return null;
    }

    /** Convert a path to a cananonical form.
     * All instances of "." and ".." are factored out.  Null is returned
     * if the path tries to .. above its root.
     * @param path the path to convert
     * @return path or null.
     */
    public static String canonicalPath(String path) {
        if (path==null || path.length()==0)
            return path;

        int end = path.length();
        int start = path.lastIndexOf('/', end);

    search:
        while (end > 0) {
            switch(end-start) {
              case 2: // possible single dot
                  if (path.charAt(start+1) != '.')
                      break;
                  break search;
              case 3: // possible double dot
                  if (path.charAt(start+1) != '.' || path.charAt(start+2) != '.')
                      break;
                  break search;
            }
            
            end = start;
            start = path.lastIndexOf('/', end-1);
        }

        // If we have checked the entire string
        if (start >= end)
            return path;
        
        StringBuilder buf = new StringBuilder(path);
        int delStart = -1;
        int delEnd = -1;
        int skip = 0;
        
        while (end > 0) {
            switch(end-start) {       
              case 2: // possible single dot
                  if (buf.charAt(start+1)!='.') {
                      if (skip > 0 && --skip == 0) {   
                          delStart = start >= 0 ? start : 0;
                          if(delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd-1) == '.')
                              delStart++;
                      }
                      break;
                  }
                  
                  if(start < 0 && buf.length() > 2 && buf.charAt(1) == '/' && buf.charAt(2) == '/')
                      break;
                  
                  if(delEnd < 0)
                      delEnd = end;
                  delStart = start;
                  if (delStart < 0 || delStart == 0 && buf.charAt(delStart) == '/') {
                      delStart++;
                      if (delEnd < buf.length() && buf.charAt(delEnd) == '/')
                          delEnd++;
                      break;
                  }
                  if (end == buf.length())
                      delStart++;
                  
                  end=start--;
                  while (start >= 0 && buf.charAt(start) != '/')
                      start--;
                  continue;
                  
              case 3: // possible double dot
                  if (buf.charAt(start+1) != '.' || buf.charAt(start+2) != '.') {
                      if (skip > 0 && --skip == 0) {
                    	  delStart = start >= 0 ? start : 0;
                          if(delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd-1) == '.')
                              delStart++;
                      }
                      break;
                  }
                  
                  delStart = start;
                  if (delEnd < 0)
                      delEnd = end;

                  skip++;
                  end = start--;
                  while (start >= 0 && buf.charAt(start) != '/')
                      start--;
                  continue;

              default:
                  if (skip > 0 && --skip == 0) {
                      delStart = start >= 0 ? start : 0;
                      if (delEnd == buf.length() && buf.charAt(delEnd-1) == '.')
                          delStart++;
                  }
            }     
            
            // Do the delete
            if (skip <= 0 && delStart >= 0 && delEnd >= delStart) {  
                buf.delete(delStart, delEnd);
                delStart = delEnd=-1;
                if (skip > 0)
                    delEnd = end;
            }
            
            end=start--;
            while (start >= 0 && buf.charAt(start) != '/')
                start--;
        }      

        // Too many ..
        if (skip > 0)
            return null;
        
        // Do the delete
        if (delEnd >= 0)
            buf.delete(delStart, delEnd);

        return buf.toString();
    }
}
