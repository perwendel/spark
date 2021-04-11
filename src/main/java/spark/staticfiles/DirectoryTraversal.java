package spark.staticfiles;

import java.nio.file.Paths;

import static spark.utils.StringUtils.removeLeadingAndTrailingSlashesFrom;

/**
 * Protecting against Directory traversal
 */
public class DirectoryTraversal {

    public static void protectAgainstInClassPath(String path, String localFolder) {
        if (!isPathWithinFolder(path, localFolder)) {
            throw new DirectoryTraversalDetection("classpath");
        }
    }

    public static void protectAgainstForExternal(String path, String externalFolder) {
    	String unixLikeFolder = unixifyPath(externalFolder);
        String nixLikePath = unixifyPath(path);
        if (!isPathWithinFolder(nixLikePath, unixLikeFolder)) {
            throw new DirectoryTraversalDetection("external");
        }
    }
    
    private static String unixifyPath(String path) {
    	return Paths.get(path).toAbsolutePath().toString().replace("\\", "/");
    }
    
    private static boolean isPathWithinFolder(String path, String folder) {
    	String rlatsPath = removeLeadingAndTrailingSlashesFrom(path);
    	String rlatsFolder = removeLeadingAndTrailingSlashesFrom(folder);
    	return rlatsPath.startsWith(rlatsFolder);
    }

    public static final class DirectoryTraversalDetection extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DirectoryTraversalDetection(String msg) {
            super(msg);
        }

    }

}
