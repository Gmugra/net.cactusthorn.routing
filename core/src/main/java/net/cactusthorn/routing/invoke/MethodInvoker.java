package net.cactusthorn.routing.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.PathTemplate.PathValues;

public final class MethodInvoker {

    private static final Logger LOG = Logger.getLogger(MethodInvoker.class.getName());

    private RoutingConfig routingConfig;

    private Class<?> clazz;

    private Method method;

    private final List<MethodParameter> parameters = new ArrayList<>();

    public MethodInvoker(RoutingConfig routingConfig, Class<?> clazz, Method method, Set<MediaType> consumesMediaTypes) {
        this.routingConfig = routingConfig;
        this.clazz = clazz;
        this.method = method;

        Parameter[] params = method.getParameters();
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (params[i].isSynthetic()) {
                continue;
            }
            parameters.add(MethodParameter.Factory.create(method, params[i], types[i], routingConfig, consumesMediaTypes));
        }
    }

    private static final String MESSAGE = "Parameter position: %s; Parameter type: %s; %s";

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {

        Object object = routingConfig.provider().provide(clazz, req);

        Object[] values = parameters.size() == 0 ? new Object[0] : new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            MethodParameter parameter = parameters.get(i);
            try {
                values[i] = parameter.findValue(req, res, con, pathValues);
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
            LOG.log(Level.SEVERE, "The problem with method invocation: {0}", new Object[] {e.getMessage()});
            throw new ServerErrorException("The problem with method invocation", Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}
