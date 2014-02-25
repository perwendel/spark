package spark.utils;

/**
* Created by jam on 2/12/14.
*/
@FunctionalInterface public interface Procedure3<T,U,V> {
    void apply (T t, U u, V v);
}
