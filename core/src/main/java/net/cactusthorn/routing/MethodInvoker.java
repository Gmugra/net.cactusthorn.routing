package net.cactusthorn.routing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.converter.*;

public final class MethodInvoker {

    private enum ParameterType {
        UNKNOWN, PATHPARAM, QUERYPARAM, REQUEST, RESPONSE, SESSION, CONTEXT
    }

    private static final class ParameterInfo {

        private ParameterType type = ParameterType.UNKNOWN;
        private Class<?> classType;
        private String name;
        private Converter<?> converter;

        private ParameterInfo(Parameter parameter, ConvertersHolder convertersHolder) {
            classType = parameter.getType();
            if (parameter.getAnnotation(PathParam.class) != null) {
                PathParam pathParam = parameter.getAnnotation(PathParam.class);
                type = ParameterType.PATHPARAM;
                name = pathParam.value();
                converter = convertersHolder.findConverter(classType);
            } else if (parameter.getAnnotation(QueryParam.class) != null) {
                QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
                type = ParameterType.QUERYPARAM;
                name = queryParam.value();
                converter = convertersHolder.findConverter(classType);
            } else if (classType == HttpServletRequest.class) {
                type = ParameterType.REQUEST;
            } else if (classType == HttpServletResponse.class) {
                type = ParameterType.RESPONSE;
            } else if (classType == HttpSession.class) {
                type = ParameterType.SESSION;
            } else if (classType == ServletContext.class) {
                type = ParameterType.CONTEXT;
            }
        }

        private Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
                throws ConverterException {

            Converter.RequestData requestData = new Converter.RequestData(req, pathValues);

            try {
                switch (type) {
                case REQUEST:
                    return req;
                case RESPONSE:
                    return res;
                case SESSION:
                    return req.getSession(false);
                case CONTEXT:
                    return con;
                case PATHPARAM:
                    return converter.convert(requestData, classType, pathValues.value(name));
                case QUERYPARAM:
                    return converter.convert(requestData, classType, req.getParameter(name));
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
