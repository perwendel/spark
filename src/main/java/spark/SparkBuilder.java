package spark;

public class SparkBuilder {
    private Spark.Api instance = new Spark.Api();

    public SparkBuilder port(int port) {
        instance.port(port);
        return this;
    }

    public SparkBuilder ip(String ip) {
        instance.ip(ip);
        return this;
    }

    public SparkBuilder secure(String keystoreFile,
                               String keystorePassword,
                               String truststoreFile,
                               String truststorePassword) {
        instance.secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
        return this;
    }

    public SparkBuilder staticFilesAt(String folder) {
        instance.staticFileLocation(folder);
        return this;
    }

    public SparkBuilder externalStaticFilesAt(String externalFolder) {
        instance.externalStaticFileLocation(externalFolder);
        return this;
    }

    public Spark.Api build() {
        return instance;
    }
}
