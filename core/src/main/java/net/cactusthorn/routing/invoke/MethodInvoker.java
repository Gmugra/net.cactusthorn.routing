package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingException;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.*;
import net.cactusthorn.routing.validate.ParametersValidationException;

public final class MethodInvoker {

    private RoutingConfig routingConfig;

    private Class<?> clazz;

    private Method method;

    private final List<MethodParameter> parameters = new ArrayList<>();

    public MethodInvoker(RoutingConfig routingConfig, Class<?> clazz, Method method, Set<MediaType> consumesMediaTypes) {
        this.routingConfig = routingConfig;
        this.clazz = clazz;
        this.method = method;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isSynthetic()) {
                continue;
            }
            parameters.add(MethodParameter.Factory.create(method, parameter, routingConfig.convertersHolder(), consumesMediaTypes));
        }
    }

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws ConverterException, ParametersValidationException {

        Object object = routingConfig.provider().provide(clazz, req);
        RequestData requestData;
        if (containsBody()) {
            requestData = new RequestData(req, pathValues, (int) routingConfig.properties().get(ConfigProperty.READ_BODY_BUFFER_SIZE));
        } else {
            requestData = new RequestData(pathValues);
        }

        Object[] values = parameters.size() == 0 ? new Object[0] : new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            MethodParameter parameter = parameters.get(i);
            try {
                values[i] = parameter.findValue(req, res, con, requestData);
            } catch (Exception e) {
                throw new ConverterException(e, i + 1, parameter.getClass().getSimpleName());
            }
        }

        if (routingConfig.validator().isPresent()) {
            routingConfig.validator().get().validate(object, method, values);
        }

        try {
            return method.invoke(object, values);
        } catch (Exception e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }

    private boolean containsBody() {
        for (MethodParameter value : parameters) {
            if (value.getClass() == BodyParameter.class) {
                return true;
            }
        }
        return false;
    }
}
