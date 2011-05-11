package spark;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("unchecked")
public class ClassPathInspector {

    static boolean DEBUG = false;

    public static List<Class> getAllKnownClasses() {
        List<Class> classFiles = new ArrayList<Class>();
        List<File> classLocations = getClassLocationsForCurrentClasspath();
        for (File file : classLocations) {
            classFiles.addAll(getClassesFromPath(file));
        }
        return classFiles;
    }

    public static List<Class> getMatchingClasses(Class interfaceOrSuperclass) {
        List<Class> matchingClasses = new ArrayList<Class>();
        List<Class> classes = getAllKnownClasses();
        log("checking %s classes", classes.size());
        for (Class clazz : classes) {
            if (interfaceOrSuperclass.isAssignableFrom(clazz)) {
                matchingClasses.add(clazz);
                log("class %s is assignable from %s", interfaceOrSuperclass, clazz);
            }
        }
        return matchingClasses;
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix,
                    Class interfaceOrSuperclass) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix) {
        throw new IllegalStateException("Not yet implemented!");
    }

    private static Collection<? extends Class> getClassesFromPath(File path) {
        if (path.isDirectory()) {
            return getClassesFromDirectory(path);
        } else {
            return getClassesFromJarFile(path);
        }
    }

    private static String fromFileToClassName(final String fileName) {
        return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
    }

    private static List<Class> getClassesFromJarFile(File path) {
        List<Class> classes = new ArrayList<Class>();
        log("getClassesFromJarFile: Getting classes for %s", path);

        try {
            if (path.canRead()) {
                JarFile jar = new JarFile(path);
                Enumeration<JarEntry> en = jar.entries();
                while (en.hasMoreElements()) {
                    JarEntry entry = en.nextElement();
                    if (entry.getName().endsWith("class")) {
                        String className = fromFileToClassName(entry.getName());
                        log("\tgetClassesFromJarFile: found %s", className);
                        loadClass(classes, className);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classes from jar file: " + path, e);
        }

        return classes;
    }

    private static List<Class> getClassesFromDirectory(File path) {
        List<Class> classes = new ArrayList<Class>();
        log("getClassesFromDirectory: Getting classes for " + path);

        // get jar files from top-level directory
        List<File> jarFiles = listFiles(path, new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        }, false);
        for (File file : jarFiles) {
            classes.addAll(getClassesFromJarFile(file));
        }

        // get all class-files
        List<File> classFiles = listFiles(path, new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        }, true);

        // List<URL> urlList = new ArrayList<URL>();
        // List<String> classNameList = new ArrayList<String>();
        int substringBeginIndex = path.getAbsolutePath().length() + 1;
        for (File classfile : classFiles) {
            String className = classfile.getAbsolutePath().substring(substringBeginIndex);
            className = fromFileToClassName(className);
            log("Found class %s in path %s: ", className, path);
            loadClass(classes, className);
        }

        return classes;
    }

    private static List<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        List<File> files = new ArrayList<File>();
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        // Return collection of files
        return files;
    }

    public static List<File> getClassLocationsForCurrentClasspath() {
        List<File> urls = new ArrayList<File>();
        String javaClassPath = System.getProperty("java.class.path");
        System.out.println("javaClassPath: " + javaClassPath);
        if (javaClassPath != null) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                urls.add(new File(path));
            }
        }
        return urls;
    }

    // todo: this is only partial, probably
    public static URL normalize(URL url) throws MalformedURLException {
        String spec = url.getFile();

        // get url base - remove everything after ".jar!/??" , if exists
        final int i = spec.indexOf("!/");
        if (i != -1) {
            spec = spec.substring(0, spec.indexOf("!/"));
        }

        // uppercase windows drive
        url = new URL(url, spec);
        final String file = url.getFile();
        final int i1 = file.indexOf(':');
        if (i1 != -1) {
            String drive = file.substring(i1 - 1, 2).toUpperCase();
            url = new URL(url, file.substring(0, i1 - 1) + drive + file.substring(i1));
        }

        return url;
    }

    private static void log(String pattern, final Object... args) {
        if (DEBUG)
            System.out.printf(pattern + "\n", args);
    }

    private static void loadClass(List<Class> classes, String className) {
        try {
            Class claz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            classes.add(claz);
        } catch (ClassNotFoundException cnfe) {
            log("ClassNotFoundException: Could not load class %s: %s", className, cnfe);
        } catch (NoClassDefFoundError e) {
            log("NoClassDefFoundError: Could not load class %s: %s", className, e);
        }
    }

}