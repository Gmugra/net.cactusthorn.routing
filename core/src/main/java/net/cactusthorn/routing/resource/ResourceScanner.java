package net.cactusthorn.routing.resource;

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
import javax.annotation.security.RolesAllowed;

import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.body.writer.Templated;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.invoke.MethodInvoker;
import net.cactusthorn.routing.invoke.MethodInvoker.ReturnObjectInfo;
import net.cactusthorn.routing.uri.PathTemplate;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class ResourceScanner {

    private static final ConsumesParser CONSUMES_PARSER = new ConsumesParser();
    private static final PathTemplateParser PATHTEMPLATE_PARSER = new PathTemplateParser();
    private static final ProducesParser PRODUCES_PARSER = new ProducesParser();

    public static final class Resource {

        private static final Comparator<Resource> COMPARATOR = (o1, o2) -> PathTemplate.COMPARATOR.compare(o1.pathTemplate,
                o2.pathTemplate);

        private PathTemplate pathTemplate;
        private MethodInvoker methodInvoker;
        private List<MediaType> producesMediaTypes;
        private Set<MediaType> consumesMediaTypes;
        private String template;
        private Set<String> rolesAllowed;

        private Resource(PathTemplate pathTemplate, String template, List<MediaType> producesMediaTypes,
                Set<MediaType> consumesMediaTypes, MethodInvoker methodInvoker, Set<String> rolesAllowed) {
            this.pathTemplate = pathTemplate;
            this.producesMediaTypes = producesMediaTypes;
            this.template = template;
            this.consumesMediaTypes = consumesMediaTypes;
            this.methodInvoker = methodInvoker;
            this.rolesAllowed = rolesAllowed;
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
            Templated templated = new Templated(template, result, req, res);
            return Response.ok(templated).build();
        }

        public ReturnObjectInfo returnObjectInfo() {
            return methodInvoker.returnObjectInfo();
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

        public Optional<MediaType> matchAccept(List<MediaType> accept) {
            for (MediaType acceptMediaType : accept) {
                for (MediaType producesMediaType : producesMediaTypes) {
                    if (acceptMediaType.isCompatible(producesMediaType)) {
                        return Optional.of(producesMediaType);
                    }
                }
            }
            return Optional.empty();
        }

        public boolean matchRolesAllowed(HttpServletRequest req) {
            if (rolesAllowed.isEmpty()) {
                return true;
            }
            return rolesAllowed.stream().filter(r -> req.isUserInRole(r)).findAny().isPresent();
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

    public ResourceScanner(RoutingConfig routingConfig) {
        this.routingConfig = routingConfig;
    }

    public Map<String, List<Resource>> scan() {

        Map<String, List<Resource>> resources = createMap();

        for (Class<?> clazz : routingConfig.resourceClasses()) {

            String classPath = PATHTEMPLATE_PARSER.prepare(routingConfig.applicationPath(), clazz.getAnnotation(Path.class));
            Set<MediaType> classConsumesMediaTypes = CONSUMES_PARSER.consumes(clazz);
            List<MediaType> classProducesMediaTypes = PRODUCES_PARSER.produces(clazz);

            for (Method method : clazz.getMethods()) {

                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {

                    String httpMethod = getHttpMethod(annotation);
                    if (httpMethod != null) {

                        PathTemplate pathTemplate = PATHTEMPLATE_PARSER.create(method, classPath);

                        List<MediaType> producesMediaTypes = PRODUCES_PARSER.produces(method, classProducesMediaTypes);

                        Set<MediaType> consumesMediaTypes = CONSUMES_PARSER.consumes(method, classConsumesMediaTypes);

                        String template = findTemplate(method);

                        MethodInvoker methodInvoker = new MethodInvoker(routingConfig, clazz, method, consumesMediaTypes);

                        Set<String> rolesAllowed = findRolesAllowed(method);

                        Resource resource = new Resource(pathTemplate, template, producesMediaTypes, consumesMediaTypes,
                                methodInvoker, rolesAllowed);
                        resources.get(httpMethod).add(resource);
                    }
                }
            }
        }

        for (Map.Entry<String, List<Resource>> entry : resources.entrySet()) {
            Collections.sort(entry.getValue(), Resource.COMPARATOR);
        }

        return resources;
    }

    private String findTemplate(Method method) {
        Template template = method.getAnnotation(Template.class);
        if (template != null) {
            return template.value();
        }
        return null;
    }

    private Set<String> findRolesAllowed(Method method) {
        RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null) {
            return new HashSet<>(Arrays.asList(rolesAllowed.value()));
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

    private Map<String, List<Resource>> createMap() {
        Map<String, List<Resource>> resources = new HashMap<>();
        for (String method : HTTP_METHODS) {
            resources.put(method, new ArrayList<>());
        }
        return resources;
    }
}
