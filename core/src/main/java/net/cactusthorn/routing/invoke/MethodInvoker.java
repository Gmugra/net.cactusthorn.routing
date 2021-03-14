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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.GenericEntity;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingServlet;
import net.cactusthorn.routing.body.writer.Templated;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.ExceptionMapperWrapper;
import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.ERROR_AT_PARAMETER_POSITION;

public final class MethodInvoker {

    private static final Logger LOG = Logger.getLogger(RoutingServlet.class.getName());

    public static final class ReturnObjectInfo {

        private Class<?> type;
        private Type genericType;
        private Annotation[] annotations;
        private Object entity;

        private ReturnObjectInfo(Method method) {
            this.type = method.getReturnType();
            this.genericType = method.getGenericReturnType();
            this.annotations = method.getAnnotations();
        }

        private ReturnObjectInfo(HttpServletRequest req, HttpServletResponse resp, ReturnObjectInfo returnObjectInfo, Object entity) {
            if (entity instanceof Templated) {
                this.type = Templated.class;
                this.entity = prepareEntity(req, resp, (Templated) entity);
            } else if (entity instanceof GenericEntity) {
                GenericEntity<?> genericEntity = (GenericEntity<?>) entity;
                this.type = genericEntity.getRawType();
                this.genericType = genericEntity.getType();
                this.annotations = returnObjectInfo.annotations();
                this.entity = genericEntity.getEntity();
            } else {
                this.type = returnObjectInfo.type();
                this.genericType = returnObjectInfo.genericType();
                this.annotations = returnObjectInfo.annotations();
                this.entity = entity;
            }
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

        public Object entity() {
            return entity;
        }

        private Templated prepareEntity(HttpServletRequest req, HttpServletResponse resp, Templated templated) {
            if (templated.request() != null && templated.response() != null) {
                return templated;
            }
            if (templated.request() != null) {
                return new Templated(templated.template(), templated.entity(), templated.request(), resp);
            }
            if (templated.response() != null) {
                return new Templated(templated.template(), templated.entity(), req, templated.response());
            }
            return new Templated(templated.template(), templated.entity(), req, resp);
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

    public ReturnObjectInfo returnObjectInfo(HttpServletRequest req, HttpServletResponse resp, Object entity) {
        return new ReturnObjectInfo(req, resp, returnObjectInfo, entity);
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
                throw new BadRequestException(Messages.msg(ERROR_AT_PARAMETER_POSITION, i + 1, parameter.getClass().getSimpleName(), e), e);
            }
        }

        if (routingConfig.validator().isPresent()) {
            routingConfig.validator().get().validate(object, method, values);
        }

        try {
            return invokeWithExceptionMappers(object, values);
        } catch (InvocationTargetException e) {
            throw new ServerErrorException(e.getCause().getMessage(), Status.INTERNAL_SERVER_ERROR, e.getCause());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            String message = Messages.msg(Messages.Key.ERROR_METHOD_INVOCATION);
            LOG.log(Level.SEVERE, message, e);
            throw new ServerErrorException(Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    private Object invokeWithExceptionMappers(Object object, Object[] values)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            return method.invoke(object, values);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            ExceptionMapperWrapper<?> mapper = (ExceptionMapperWrapper<?>) routingConfig.providers().getExceptionMapper(cause.getClass());
            if (mapper != null) {
                return mapper.response(cause);
            }
            throw exception;
        }
    }
}
