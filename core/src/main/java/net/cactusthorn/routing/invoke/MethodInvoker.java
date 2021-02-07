package net.cactusthorn.routing.invoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.Templated;
import net.cactusthorn.routing.PathTemplate.PathValues;

public final class MethodInvoker {

    private static final Logger LOG = Logger.getLogger(MethodInvoker.class.getName());

    public static final class ReturnObjectInfo {

        public static final ReturnObjectInfo TEMPLATED = new ReturnObjectInfo(Templated.class, null, null);

        private Class<?> type;
        private Type genericType;
        private Annotation[] annotations;

        private ReturnObjectInfo(Method method) {
            this.type = method.getReturnType();
            this.genericType = method.getGenericReturnType();
            this.annotations = method.getAnnotations();
        }

        private ReturnObjectInfo(Class<?> type, Type genericType, Annotation[] annotations) {
            this.type = type;
            this.genericType = genericType;
            this.annotations = annotations;
        }

        public ReturnObjectInfo withEntity(Object entity) {
            if (entity instanceof Templated) {
                return ReturnObjectInfo.TEMPLATED;
            }
            if (Response.class.isAssignableFrom(type)) {
                return new ReturnObjectInfo(entity.getClass(), null, annotations);
            }
            return this;
        }

        public Class<?> type() {
            return type;
        }

        public Type genericType() {
            return genericType;
        }

        public Annotation[] annotations() {
            return annotations;
        }
    }

    private RoutingConfig routingConfig;

    private Class<?> clazz;

    private Method method;

    private List<MethodParameter> parameters;

    private ReturnObjectInfo returnObjectInfo;

    public MethodInvoker(RoutingConfig routingConfig, Class<?> clazz, Method method, Set<MediaType> consumesMediaTypes) {
        this.routingConfig = routingConfig;
        this.clazz = clazz;
        this.method = method;

        returnObjectInfo = new ReturnObjectInfo(method);

        parameters = MethodParameterFactory.create(method, routingConfig, consumesMediaTypes);
    }

    private static final String MESSAGE = "Parameter position: %s; Parameter type: %s; %s";

    public ReturnObjectInfo returnObjectInfo() {
        return returnObjectInfo;
    }

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {

        Object object = routingConfig.provider().provide(clazz, req);

        Object[] values = parameters.size() == 0 ? new Object[0] : new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            MethodParameter parameter = parameters.get(i);
            try {
                values[i] = parameter.findValue(req, res, con, pathValues);
            } catch (ClientErrorException e) {
                throw e;
            } catch (Exception e) {
                throw new BadRequestException(String.format(MESSAGE, i + 1, parameter.getClass().getSimpleName(), e), e);
            }
        }

        if (routingConfig.validator().isPresent()) {
            routingConfig.validator().get().validate(object, method, values);
        }

        try {
            return method.invoke(object, values);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOG.log(Level.SEVERE, "The problem with method invocation", e);
            throw new ServerErrorException("The problem with method invocation", Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
