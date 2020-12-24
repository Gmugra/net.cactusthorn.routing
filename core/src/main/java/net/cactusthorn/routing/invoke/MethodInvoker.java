package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingException;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.convert.*;

public final class MethodInvoker {

    private ComponentProvider componentProvider;

    private Class<?> clazz;

    private Method method;

    private final List<MethodParameter> parameters = new ArrayList<>();

    private Map<ConfigProperty, Object> configProperties;

    public MethodInvoker(Class<?> clazz, Method method, ComponentProvider componentProvider, ConvertersHolder convertersHolder,
            String contentType, Map<ConfigProperty, Object> configProperties) {
        this.clazz = clazz;
        this.method = method;
        this.componentProvider = componentProvider;
        this.configProperties = configProperties;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isSynthetic()) {
                continue;
            }
            parameters.add(MethodParameter.Factory.create(method, parameter, convertersHolder, contentType));
        }
    }

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws ConverterException {

        Object object = componentProvider.provide(clazz);
        RequestData requestData;
        if (containsBody()) {
            requestData = new RequestData(req, pathValues, (int) configProperties.get(ConfigProperty.READ_BODY_BUFFER_SIZE));
        } else {
            requestData = new RequestData(pathValues);
        }

        Object[] values;
        if (parameters.size() == 0) {
            values = new Object[0];
        } else {
            values = new Object[parameters.size()];
        }

        for (int i = 0; i < parameters.size(); i++) {
            values[i] = parameters.get(i).findValue(req, res, con, requestData);
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
