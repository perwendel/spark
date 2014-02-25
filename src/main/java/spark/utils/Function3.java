package spark.utils;

/**
* Created by jam on 2/12/14.
*/
@FunctionalInterface public interface Function3<T,U,V,R> {
    R apply (T t, U u, V v);
}
