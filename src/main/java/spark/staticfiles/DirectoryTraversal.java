package spark.staticfiles;

import java.nio.file.Paths;

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
        String nixLikePath = Paths.get(path).toAbsolutePath().toString().replace("\\", "/");
        if (!removeLeadingAndTrailingSlashesFrom(nixLikePath).startsWith(StaticFilesFolder.external())) {
            throw new DirectoryTraversalDetection("external");
        }
    }

    public static final class DirectoryTraversalDetection extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DirectoryTraversalDetection(String msg) {
            super(msg);
        }

    }

}
