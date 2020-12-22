package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

/**
 * Binds the value(s) of a HTTP query parameter to a resource method parameter. 
 */
@Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface QueryParam {
    String value();
}
