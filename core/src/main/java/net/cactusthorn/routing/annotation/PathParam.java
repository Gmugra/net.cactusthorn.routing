package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

/**
 * Binds the value of a URI template parameter to a method parameter.
 */
@Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface PathParam {
    String value();
}
