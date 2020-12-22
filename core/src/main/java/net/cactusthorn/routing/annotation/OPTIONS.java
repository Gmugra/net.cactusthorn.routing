package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

/**
 * Indicates that the annotated method responds to HTTP OPTIONS requests.
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface OPTIONS {
}
