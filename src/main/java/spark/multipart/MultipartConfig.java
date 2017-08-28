package spark.multipart;

/**
 * Defines the various options for configuring multipart requests/uploads.
 */
public class MultipartConfig {
    private String tempDirectoryPath;
    private long maxPartSize;
    private long maxRequestSize;
    private long fileSizeThreshold;

    /**
     * Creates a config with the default values that are good enough for most apps.
     */
    public MultipartConfig() {
        setTempDirectoryPath("/temp");

        // These are the default values Jetty uses for all 3 of these properties, so use those for now.
        setMaxPartSize(-1);
        setMaxRequestSize(-1);
        setFileSizeThreshold(0);
    }

    /**
     * The path to the temporary directory (usually something like "/temp"
     *
     * @return The temp directory path
     */
    public String getTempDirectoryPath() {
        return tempDirectoryPath;
    }

    /**
     * Defines the path to the temporary directory (usually something like "/temp"
     *
     * @param tempDirectoryPath The temp directory path
     */
    public void setTempDirectoryPath(String tempDirectoryPath) {
        this.tempDirectoryPath = tempDirectoryPath;
    }

    /**
     * The maximum size for a single part that the request will accept.
     *
     * @return The max size in bytes or -1 if there is no limit
     */
    public long getMaxPartSize() {
        return maxPartSize;
    }

    /**
     * Defines the maximum size for a single part that the request will accept.
     *
     * @param maxPartSize The max size in bytes or -1 if there is no limit
     */
    public void setMaxPartSize(long maxPartSize) {
        this.maxPartSize = maxPartSize;
    }

    /**
     * The maximum size for the entire multipart request that will be accepted
     *
     * @return The max size in bytes or -1 if there is no limit
     */
    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * Defines the maximum size for the entire multipart request that will be accepted
     *
     *  or -1 if there is no limit@param maxRequestSize The max size in bytes
     */
    public void setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    /**
     * Defines how many bytes a single file/part can be before we stop loading it in memory and write it to the
     * temp directory instead.
     *
     * @return The file size threshold in bytes
     */
    public long getFileSizeThreshold() {
        return fileSizeThreshold;
    }

    /**
     * Defines how many bytes a single file/part can be before we stop loading it in memory and write it to the
     * temp directory instead.
     *
     * @param fileSizeThreshold The file size threshold in bytes
     */
    public void setFileSizeThreshold(long fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }


    // ------ Chaining support -------------------

    /**
     * Chaining support. Defines the path ot the temp directory.
     *
     * @param path The temp directory path
     * @return this
     */
    public MultipartConfig tempPath(String path) {
        setTempDirectoryPath(path);
        return this;
    }

    /**
     * Chaining support. Defines the maximum size of a single part.
     *
     * @param size The size in bytes
     * @return this
     */
    public MultipartConfig maxPartSize(long size) {
        setMaxPartSize(size);
        return this;
    }

    /**
     * Chaining support. Defines the maximum size of an entire multipart request.
     *
     * @param size The size in bytes
     * @return this
     */
    public MultipartConfig maxRequestSize(long size) {
        setMaxRequestSize(size);
        return this;
    }

    /**
     * Chaining support. Defines the maximum size of an entire multipart request.
     *
     * @param size The size in bytes
     * @return this
     */
    public MultipartConfig fileThreshold(long size) {
        setFileSizeThreshold(size);
        return this;
    }
}
