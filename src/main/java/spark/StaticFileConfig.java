package spark;

public class StaticFileConfig {
    private final String staticFileFolder;
    private final String externalStaticFileFolder;

    /**
     * @param staticFileFolder the folder in classpath serving static files.
     * @param externalStaticFileFolder the external folder serving static files.
     */

    public StaticFileConfig(String staticFileFolder, String externalStaticFileFolder) {
        this.staticFileFolder = staticFileFolder;
        this.externalStaticFileFolder = externalStaticFileFolder;
    }

    public String getStaticFileFolder() {
        return staticFileFolder;
    }

    public String getExternalStaticFileFolder() {
        return externalStaticFileFolder;
    }

    public boolean hasMultipleHandlers() {
        return staticFileFolder != null || externalStaticFileFolder != null;
    }
}
