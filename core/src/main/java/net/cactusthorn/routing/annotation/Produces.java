package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD }) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface Produces {
    String value() default "text/plain";
}
