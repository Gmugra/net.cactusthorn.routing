package net.cactusthorn.routing.resource;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.uri.PathTemplate;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.PATH_TEMPLATE_INCORRECT;

public final class PathTemplateParser {

    PathTemplate create(Method method, String classPath) {
        try {
            String path = classPath.substring(0, classPath.length() - 1) + prepareMethodPath(method.getAnnotation(Path.class));
            return new PathTemplate(path);
        } catch (IllegalArgumentException e) {
            throw new RoutingInitializationException(Messages.msg(PATH_TEMPLATE_INCORRECT, method), e);
        }
    }

    String prepare(String applicationPath, Path classPathAnnotation) {
        if (classPathAnnotation == null) {
            return applicationPath;
        }
        String path = classPathAnnotation.value();
        if (path.charAt(0) != '/') {
            path = applicationPath + path;
        } else {
            path = applicationPath.substring(0, applicationPath.length() - 1) + path;
        }
        if (!"/".equals(path) && path.charAt(path.length() - 1) != '/') {
            path += '/';
        }
        return path;
    }

    private String prepareMethodPath(Path methodPathAnnotation) {
        if (methodPathAnnotation == null) {
            return "/";
        }
        String path = methodPathAnnotation.value();
        if (path.charAt(0) != '/') {
            path = '/' + path;
        }
        if (!"/".equals(path) && path.charAt(path.length() - 1) != '/') {
            path += '/';
        }
        return path;
    }
}
