package net.cactusthorn.routing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.converter.*;

public final class MethodInvoker {

    private enum ParameterType {
        UNKNOWN, PATHPARAM, QUERYPARAM, CONTEXT
    }

    private static final class ParameterInfo {

        private ParameterType type = ParameterType.UNKNOWN;
        private String name;
        private Converter<?> converter;

        private ParameterInfo(Parameter parameter, ConvertersHolder convertersHolder) {
            converter = convertersHolder.findConverter(parameter);
            if (parameter.getAnnotation(Context.class) != null) {
                type = ParameterType.CONTEXT;
            } else if (parameter.getAnnotation(PathParam.class) != null) {
                PathParam pathParam = parameter.getAnnotation(PathParam.class);
                type = ParameterType.PATHPARAM;
                name = pathParam.value();
            } else if (parameter.getAnnotation(QueryParam.class) != null) {
                QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                type = ParameterType.QUERYPARAM;
                name = queryParam.value();
            }
        }

        private Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
                throws ConverterException {
            try {
                switch (type) {
                case CONTEXT:
                    return converter.convert(req, res, con, null);
                case PATHPARAM:
                    return converter.convert(req, res, con, pathValues.value(name));
                case QUERYPARAM:
                    return converter.convert(req, res, con, req.getParameter(name));
                default:
                    return null;
                }
            } catch (Exception e) {
                throw new ConverterException("Parameter convertiong problem", e);
            }
        }
    }

    private ComponentProvider componentProvider;

    private Class<?> clazz;

    private Method method;

    private final List<ParameterInfo> parameters = new ArrayList<>();

    public MethodInvoker(Class<?> clazz, Method method, ComponentProvider componentProvider, ConvertersHolder convertersHolder) {
        this.clazz = clazz;
        this.method = method;
        this.componentProvider = componentProvider;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isSynthetic()) {
                continue;
            }
            parameters.add(new ParameterInfo(parameter, convertersHolder));
        }
    }

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws ConverterException {
        try {

            Object object = componentProvider.provide(clazz);

            Object[] values;
            if (parameters.size() == 0) {
                values = new Object[0];
            } else {
                values = new Object[parameters.size()];
            }

            for (int i = 0; i < parameters.size(); i++) {
                values[i] = parameters.get(i).findValue(req, res, con, pathValues);
            }

            return method.invoke(object, values);
        } catch (ConverterException ce) {
            throw ce;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }
}
