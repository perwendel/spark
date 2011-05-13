package spark.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

class ReflectionsAnnotationFinder implements AnnotationFinder {

    public <T extends Annotation> Set<Method> findMethodsAnnotatedWith(Class<T> clazz) {
        Reflections reflections = new Reflections("", new MethodAnnotationsScanner());
        Set<Method> annotated = reflections.getMethodsAnnotatedWith(clazz);
        return annotated;
    }

}
