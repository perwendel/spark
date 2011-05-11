package spark.routefinder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public interface AnnotationFinder {

    <T extends Annotation> Set<Method> findMethodsAnnotatedWith(Class<T> clazz);
    
}
