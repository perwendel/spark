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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import spark.utils.StringUtils;

/**
 * Created by Per Wendel on 2014-05-18.
 */
public class ExternalResource extends AbstractFileResolvingResource {

    private final File file;

    /**
     * Constructor
     *
     * @param path the path to the external resource
     */
    public ExternalResource(String path) {
        file = new File(StringUtils.cleanPath(path));
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a
     * directory.
     *
     * @return true if and only if the file denoted by this
     * abstract pathname exists and is a directory;
     * false otherwise
     */
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public String getDescription() {
        return "external resource [" + file.getAbsolutePath() + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * This implementation returns a URL for the underlying class path resource.
     *
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    @Override
    public URL getURL() throws IOException {
        return file.toURI().toURL();
    }

    /**
     * Gets the path
     *
     * @return the path
     */
    public String getPath() {
        return file.getPath();
    }
}
