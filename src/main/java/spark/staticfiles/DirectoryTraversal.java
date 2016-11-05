package spark.staticfiles;

/**
 * Protecting against Directory traversal
 */
public class DirectoryTraversal {

    public static void protectAgainstInClassPath(String path) {
        if (!path.startsWith(StaticFilesFolder.local())) {
            throw new DirectoryTraversalDetection();
        }
    }

    public static void protectAgainstForExternal(String path) {
        if (!path.startsWith(StaticFilesFolder.external())) {
            throw new DirectoryTraversalDetection();
        }
    }

    public static final class DirectoryTraversalDetection extends RuntimeException {

    }

}
