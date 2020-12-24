package net.cactusthorn.routing.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used to inject context information into a method parameter.
 *
 * Fore the moment only next classes can be injected:
 * {@link javax.servlet.http.HttpServletRequest}
 * {@link javax.servlet.http.HttpServletResponse}
 * {@link javax.servlet.ServletContext}
 * {@link javax.servlet.http.HttpSession}
 *
 */
@Target(ElementType.PARAMETER) @Retention(RetentionPolicy.RUNTIME) @Documented //
public @interface Context {
}
