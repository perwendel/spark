package spark.routefinder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.util.ClasspathHelper;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import spark.Spark;

@SuppressWarnings("unchecked")
class ScannotationFinder implements AnnotationFinder {

    public <T extends Annotation> Set<Method> findMethodsAnnotatedWith(Class<T> annotationClazz) {
        Set<Method> annotated = new HashSet<Method>();
        HashSet<URL> urlSet = new HashSet<URL>();
        urlSet.addAll(ClasspathHelper.getUrlsForCurrentClasspath());

        // TODO: is this needed, perhaps redundant ???
        URL[] urls = ClasspathUrlFinder.findClassPaths(); // scan java.class.path
        for (URL url : urls) {
            urlSet.add(url);
        }

        AnnotationDB db = new AnnotationDB();
        db.addIgnoredPackages("java", "javax");
        try {
            db.scanArchives(urlSet.toArray(new URL[]{}));

            Set<String> entities = db.getAnnotationIndex().get(annotationClazz.getName());

            List<Class> classes = new ArrayList<Class>();
            for (String entity : entities) {
                loadClass(classes, entity);
            }

            for (Class clazz : classes) {
                try {
                    for (Method m : clazz.getMethods()) {
                        T annotation = m.getAnnotation(annotationClazz);
                        if (annotation != null) {
                            annotated.add(m);
                        }
                    }
                } catch (Exception e) {}
                System.out.println(clazz);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return annotated;
    }

    private static void loadClass(List<Class> classes, String className) {
        try {
            Class clazz = loadClass(Spark.class.getClassLoader(), className);
            if (clazz == null) {
                clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            }
            classes.add(clazz);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static Class loadClass(ClassLoader loader, String className) throws ClassNotFoundException {
        Class claz = Class.forName(className, false, loader);
        return claz;
    }

}
