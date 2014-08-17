package spark.resource;

/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import spark.utils.ResourceUtils;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as UrlResource or {@link ClassPathResource}.
 * <p>Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * @author Juergen Hoeller
 * Code copied from Spring source. Modifications made (mostly removal of methods) by Per Wendel.
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

    /**
     * This implementation returns a File reference for the underlying class path
     * resource, provided that it refers to a file in the file system.
     */
    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        return ResourceUtils.getFile(url, getDescription());
    }

    /**
     * This implementation determines the underlying File
     * (or jar file, in case of a resource in a jar/zip).
     */
    @Override
    protected File getFileForLastModifiedCheck() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isJarURL(url)) {
            URL actualUrl = ResourceUtils.extractJarFileURL(url);
            return ResourceUtils.getFile(actualUrl, "Jar URL");
        } else {
            return getFile();
        }
    }


    @Override
    public boolean exists() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                // Proceed with file system resolution...
                return getFile().exists();
            } else {
                // Try a URL connection content-length header...
                URLConnection con = url.openConnection();
                customizeConnection(con);
                HttpURLConnection httpCon =
                        (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
                if (httpCon != null) {
                    int code = httpCon.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        return true;
                    } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                        return false;
                    }
                }
                if (con.getContentLength() >= 0) {
                    return true;
                }
                if (httpCon != null) {
                    // no HTTP OK status, and no content-length header: give up
                    httpCon.disconnect();
                    return false;
                } else {
                    // Fall back to stream existence: can we open the stream?
                    InputStream is = getInputStream();
                    is.close();
                    return true;
                }
            }
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                // Proceed with file system resolution...
                File file = getFile();
                return (file.canRead() && !file.isDirectory());
            } else {
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public long contentLength() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            // Proceed with file system resolution...
            return getFile().length();
        } else {
            // Try a URL connection content-length header...
            URLConnection con = url.openConnection();
            customizeConnection(con);
            return con.getContentLength();
        }
    }

    @Override
    public long lastModified() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
            // Proceed with file system resolution...
            return super.lastModified();
        } else {
            // Try a URL connection last-modified header...
            URLConnection con = url.openConnection();
            customizeConnection(con);
            return con.getLastModified();
        }
    }


    /**
     * Customize the given {@link URLConnection}, obtained in the course of an
     * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
     * <p>Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
     * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
     * Can be overridden in subclasses.
     *
     * @param con the URLConnection to customize
     * @throws IOException if thrown from URLConnection methods
     */
    protected void customizeConnection(URLConnection con) throws IOException {
        ResourceUtils.useCachesIfNecessary(con);
        if (con instanceof HttpURLConnection) {
            customizeConnection((HttpURLConnection) con);
        }
    }

    /**
     * Customize the given {@link HttpURLConnection}, obtained in the course of an
     * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
     * <p>Sets request method "HEAD" by default. Can be overridden in subclasses.
     *
     * @param con the HttpURLConnection to customize
     * @throws IOException if thrown from HttpURLConnection methods
     */
    protected void customizeConnection(HttpURLConnection con) throws IOException {
        con.setRequestMethod("HEAD");
    }

}
