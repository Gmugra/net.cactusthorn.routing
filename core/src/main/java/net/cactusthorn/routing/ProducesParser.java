package net.cactusthorn.routing;

import java.lang.reflect.Method;

import net.cactusthorn.routing.annotation.Produces;

public final class ProducesParser {

    public static final String PRODUCES_DEFAULT = "text/plain";

    String produces(Method method) {
        Produces produces = method.getAnnotation(Produces.class);
        if (produces != null) {
            return produces.value();
        }
        return PRODUCES_DEFAULT;
    }
}
