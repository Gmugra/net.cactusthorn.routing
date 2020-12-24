package net.cactusthorn.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.invoke.MethodInvoker;

public class EntryPointScanner {

    public final static class EntryPoint {

        private static final Comparator<EntryPoint> COMPARATOR = (o1, o2) -> Template.COMPARATOR.compare(o1.template, o2.template);

        private Template template;
        private MethodInvoker methodInvoker;
        private String produces;
        private String contentType;
        private Pattern contentTypePattern;

        private EntryPoint(Class<?> clazz, Method method, ComponentProvider componentProvider, String template,
                ConvertersHolder convertersHolder, String produces, String contentType, Map<ConfigProperty, Object> configProperties) {
            try {
                this.produces = produces;
                this.contentType = contentType;
                contentTypePattern = Pattern.compile(contentType.replace("*", "(.*)"));
                methodInvoker = new MethodInvoker(clazz, method, componentProvider, convertersHolder, contentType, configProperties);
                this.template = new Template(template);
            } catch (Exception e) {
                throw new RoutingException("Initialization problem at the Method: " + method, e);
            }
        }

        public boolean match(String path) {
            return template.match(path);
        }

        public PathValues parse(String path) {
            return template.parse(path);
        }

        public String template() {
            return template.pattern().pattern();
        }

        public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
                throws ConverterException {
            return methodInvoker.invoke(req, res, con, pathValues);
        }

        public String produces() {
            return produces;
        }

        public String consumes() {
            return contentType;
        }

        public boolean matchContentType(String contentType) {
            if (this.contentType.equals(contentType)) {
                return true;
            }
            return contentTypePattern.matcher(contentType).find();
        }
    }

    private static final Set<Class<? extends Annotation>> HTTP_ANNOTATTIONS = new HashSet<>(
            Arrays.asList(DELETE.class, GET.class, HEAD.class, OPTIONS.class, PATCH.class, POST.class, PUT.class, TRACE.class));

    private final List<Class<?>> classes = new ArrayList<>();
    private ConvertersHolder convertersHolder;
    private ComponentProvider componentProvider;
    private Map<ConfigProperty, Object> configProperties;

    public EntryPointScanner(Collection<Class<?>> classes, ComponentProvider componentProvider, ConvertersHolder convertersHolder,
            Map<ConfigProperty, Object> configProperties) {
        this.classes.addAll(classes);
        this.componentProvider = componentProvider;
        this.convertersHolder = convertersHolder;
        this.configProperties = configProperties;
    }

    public Map<Class<? extends Annotation>, List<EntryPoint>> scan() {

        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = createMap();

        for (Class<?> clazz : classes) {

            String classPath = preparePath(clazz.getAnnotation(Path.class));
            String clazzConsumes = findConsumes(clazz);

            for (Method method : clazz.getMethods()) {

                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {

                    Class<? extends Annotation> type = annotation.annotationType();
                    if (HTTP_ANNOTATTIONS.contains(type)) {

                        String path = classPath + preparePath(method.getAnnotation(Path.class));
                        if (path.isEmpty()) {
                            path += '/';
                        }

                        String produces = findProduces(method);
                        String consumes = findConsumes(method, clazzConsumes);

                        EntryPoint entryPoint = new EntryPoint(clazz, method, componentProvider, path, convertersHolder, produces,
                                consumes, configProperties);
                        entryPoints.get(type).add(entryPoint);
                    }
                }
            }
        }

        for (Map.Entry<Class<? extends Annotation>, List<EntryPoint>> entry : entryPoints.entrySet()) {
            Collections.sort(entry.getValue(), EntryPoint.COMPARATOR);
        }

        return entryPoints;
    }

    public static final String CONSUMES_DEFAULT = "*/*";

    private String findConsumes(Class<?> clazz) {
        Consumes consumes = clazz.getAnnotation(Consumes.class);
        if (consumes != null) {
            return consumes.value();
        }
        return CONSUMES_DEFAULT;
    }

    private String findConsumes(Method method, String clazzConsumes) {
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes != null) {
            return consumes.value();
        }
        return clazzConsumes;
    }

    public static final String PRODUCES_DEFAULT = "text/plain";

    private String findProduces(Method method) {
        Produces produces = method.getAnnotation(Produces.class);
        if (produces != null) {
            return produces.value();
        }
        return PRODUCES_DEFAULT;
    }

    private Map<Class<? extends Annotation>, List<EntryPoint>> createMap() {
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = new HashMap<>();
        for (Class<? extends Annotation> annotation : HTTP_ANNOTATTIONS) {
            entryPoints.put(annotation, new ArrayList<>());
        }
        return entryPoints;
    }

    private String preparePath(Path classPathAnnotation) {
        if (classPathAnnotation == null) {
            return "";
        }
        String path = classPathAnnotation.value();
        if (path.charAt(0) != '/') {
            path = '/' + path;
        }
        if (path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

}
