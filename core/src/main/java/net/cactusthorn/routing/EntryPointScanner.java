package net.cactusthorn.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.annotation.UserRoles;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.invoke.MethodInvoker;

public class EntryPointScanner {

    private static final ConsumesParser CONSUMES_PARSER = new ConsumesParser();
    private static final PathTemplateParser PATHTEMPLATE_PARSER = new PathTemplateParser();
    private static final ProducesParser PRODUCES_PARSER = new ProducesParser();

    public static final class EntryPoint {

        private static final Comparator<EntryPoint> COMPARATOR = (o1, o2) -> PathTemplate.COMPARATOR.compare(o1.pathTemplate,
                o2.pathTemplate);

        private PathTemplate pathTemplate;
        private MethodInvoker methodInvoker;
        private String produces;
        private Set<MediaType> consumesMediaTypes;
        private String template;
        private Set<String> userRoles;

        private EntryPoint(PathTemplate pathTemplate, String produces, String template, Set<MediaType> consumesMediaTypes,
                MethodInvoker methodInvoker, Set<String> userRoles) {
            this.pathTemplate = pathTemplate;
            this.produces = produces;
            this.template = template;
            this.consumesMediaTypes = consumesMediaTypes;
            this.methodInvoker = methodInvoker;
            this.userRoles = userRoles;
        }

        public boolean match(String path) {
            return pathTemplate.match(path);
        }

        public PathValues parse(String path) {
            return pathTemplate.parse(path);
        }

        public String pathTemplatePattern() {
            return pathTemplate.pattern().pattern();
        }

        public String template() {
            return template;
        }

        public Response invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
            Object result = methodInvoker.invoke(req, res, con, pathValues);
            if (result instanceof Response) {
                return (Response) result;
            }
            if (result != null && template == null) {
                return Response.ok(result).build();
            }
            if (result == null && template == null) {
                return Response.status(Status.NO_CONTENT).build();
            }
            Templated templated = new Templated(req, res, template, result);
            return Response.ok(templated).build();
        }

        public String produces() {
            return produces;
        }

        public boolean matchUserRole(HttpServletRequest req) {
            if (userRoles.isEmpty()) {
                return true;
            }
            return userRoles.stream().filter(r -> req.isUserInRole(r)).findAny().isPresent();
        }

        public boolean matchContentType(String contenttype) {
            try {
                MediaType ct = MediaType.valueOf(contenttype);
                for (MediaType mediaType : consumesMediaTypes) {
                    if (mediaType.isCompatible(ct)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                throw new BadRequestException(HttpHeaders.CONTENT_TYPE + ": " + e.getMessage());
            }
        }
    }

    private RoutingConfig routingConfig;

    public EntryPointScanner(RoutingConfig routingConfig) {
        this.routingConfig = routingConfig;
    }

    public Map<String, List<EntryPoint>> scan() {

        Map<String, List<EntryPoint>> entryPoints = createMap();

        for (Class<?> clazz : routingConfig.entryPointClasses()) {

            String classPath = PATHTEMPLATE_PARSER.prepare(routingConfig.applicationPath(), clazz.getAnnotation(Path.class));
            Set<MediaType> classConsumesMediaTypes = CONSUMES_PARSER.consumes(clazz);

            for (Method method : clazz.getMethods()) {

                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {

                    String httpMethod = getHttpMethod(annotation);
                    if (httpMethod != null) {

                        PathTemplate pathTemplate = PATHTEMPLATE_PARSER.create(method, classPath);

                        String produces = PRODUCES_PARSER.produces(method);

                        Set<MediaType> consumesMediaTypes = CONSUMES_PARSER.consumes(method, classConsumesMediaTypes);

                        String template = findTemplate(method);

                        MethodInvoker methodInvoker = new MethodInvoker(routingConfig, clazz, method, consumesMediaTypes);

                        Set<String> userRoles = findUserRoles(method);

                        EntryPoint entryPoint = new EntryPoint(pathTemplate, produces, template, consumesMediaTypes, methodInvoker,
                                userRoles);
                        entryPoints.get(httpMethod).add(entryPoint);
                    }
                }
            }
        }

        for (Map.Entry<String, List<EntryPoint>> entry : entryPoints.entrySet()) {
            Collections.sort(entry.getValue(), EntryPoint.COMPARATOR);
        }

        return entryPoints;
    }

    private String findTemplate(Method method) {
        Template template = method.getAnnotation(Template.class);
        if (template != null) {
            return template.value();
        }
        return null;
    }

    private Set<String> findUserRoles(Method method) {
        UserRoles userRoles = method.getAnnotation(UserRoles.class);
        if (userRoles != null) {
            return new HashSet<>(Arrays.asList(userRoles.value()));
        }
        return Collections.emptySet();
    }

    private static final Set<String> HTTP_METHODS = new HashSet<>(Arrays.asList(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.HEAD,
            HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH));

    private String getHttpMethod(Annotation annotation) {
        HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
        if (httpMethod != null && HTTP_METHODS.contains(httpMethod.value())) {
            return httpMethod.value();
        }
        return null;
    }

    private Map<String, List<EntryPoint>> createMap() {
        Map<String, List<EntryPoint>> entryPoints = new HashMap<>();
        for (String method : HTTP_METHODS) {
            entryPoints.put(method, new ArrayList<>());
        }
        return entryPoints;
    }
}
