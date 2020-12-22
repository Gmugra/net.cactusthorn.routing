package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD }) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface Consumes {
    String value() default "*/*";
}
