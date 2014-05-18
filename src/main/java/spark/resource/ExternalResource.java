package spark.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import spark.utils.StringUtils;

/**
 * Created by Per Wendel on 2014-05-18.
 */
public class ExternalResource extends AbstractFileResolvingResource {

    private final File file;

    public static void main(String[] args) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        System.out.println("tempDir = " + tempDir);
        ExternalResource resource = new ExternalResource(tempDir + "/externalFile.html");
        System.out.println("resource.exists() = " + resource.exists());
        System.out.println("resource.isDirectory() = " + resource.isDirectory());
        System.out.println("resource.getURL() = " + resource.getURL());
        System.out.println("resource.getURI() = " + resource.getURI());
        System.out.println("resource.getPath() = " + resource.getPath());
    }


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
     * @return <code>true</code> if and only if the file denoted by this
     * abstract pathname exists <em>and</em> is a directory;
     * <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
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
