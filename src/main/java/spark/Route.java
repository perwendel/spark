package spark;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD})

public @interface Route {
    String value();
}
