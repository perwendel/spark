package spark.staticfiles;

/**
 * Created by Per Wendel on 2016-11-05.
 */
public class StaticFilesFolder {

    private static volatile String local;
    private static volatile String external;

    public static final void localConfiguredTo(String folder) {
        local = folder;

        if (local.startsWith("/")) {
            local = local.substring(1);
        }
    }

    public static final void externalConfiguredTo(String folder) {
        external = folder;
    }

    public static final String local() {
        return local;
    }

    public static final String external() {
        return external;
    }


}
