package spark.http.matching;

//CS304 Issue link: https://github.com/perwendel/spark/issues/911
public class Configuration {
    private String defaultContentType;
    private static final Configuration configuration = new Configuration();

    private Configuration() {
        defaultContentType = "text/html; charset=utf-8";
    }

    public static Configuration getConfiguration(){
        return configuration;
    }

    public void setDefaultContentType(String type) {
        defaultContentType = type;
    }

    public String getDefaultContentType() {
        return defaultContentType;
    }
}
