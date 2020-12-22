package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

/**
 * Identifies the URI path that a resource class or class method will serve
 * requests for.
 *
 * Paths are relative. For an annotated class the base URI is the "servlet-mapping"
 * path. For an annotated method the base URI is
 * the effective URI of the containing class. For the purposes of absolutizing a
 * path against the base URI , a leading '/' in a path is ignored and base URIs
 * are treated as if they ended in '/'.
 */
@Target({ ElementType.TYPE, ElementType.METHOD }) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface Path {
    String value();
}
