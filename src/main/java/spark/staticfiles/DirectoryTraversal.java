package spark.staticfiles;

import static spark.utils.StringUtils.removeLeadingAndTrailingSlashesFrom;

/**
 * Protecting against Directory traversal
 */
public class DirectoryTraversal {

    public static void protectAgainstInClassPath(String path) {
        if (!removeLeadingAndTrailingSlashesFrom(path).startsWith(StaticFilesFolder.local())) {
            throw new DirectoryTraversalDetection("classpath");
        }
    }

    public static void protectAgainstForExternal(String path) {
        if (!removeLeadingAndTrailingSlashesFrom(path).startsWith(StaticFilesFolder.external())) {
            throw new DirectoryTraversalDetection("external");
        }
    }

    public static final class DirectoryTraversalDetection extends RuntimeException {

        public DirectoryTraversalDetection(String msg) {
            super(msg);
        }

    }

}
