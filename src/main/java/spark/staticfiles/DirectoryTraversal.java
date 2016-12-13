package spark.staticfiles;

import static spark.utils.StringUtils.removeLeadingAndTrailingSlashesFrom;
import java.nio.file.Paths;

/**
 * Protecting against Directory traversal
 */
public class DirectoryTraversal {

    public static void protectAgainstInClassPath(String path, String filesFolder) {

        if (!removeLeadingAndTrailingSlashesFrom(path).startsWith(removeLeadingAndTrailingSlashesFrom(filesFolder))) {
            throw new DirectoryTraversalDetection("classpath");
        }
    }

    public static void protectAgainstForExternal(String path, String filesFolder) {
        String nixLikePath = Paths.get(path).toAbsolutePath().toString().replace("\\", "/");
        if (!removeLeadingAndTrailingSlashesFrom(nixLikePath).startsWith(removeLeadingAndTrailingSlashesFrom(filesFolder))) {
            throw new DirectoryTraversalDetection("external");
        }
    }

    public static final class DirectoryTraversalDetection extends RuntimeException {

        public DirectoryTraversalDetection(String msg) {
            super(msg);
        }

    }

}
