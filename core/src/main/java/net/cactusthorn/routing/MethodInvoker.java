package net.cactusthorn.routing;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.converter.*;

public final class MethodInvoker {

    private enum ParameterType {
        UNKNOWN, PATHPARAM, QUERYPARAM, REQUEST, RESPONSE, SESSION, CONTEXT, BODY
    }

    private static final class ParameterInfo {

        private ParameterType type = ParameterType.UNKNOWN;
        private Class<?> classType;
        private String name;
        private Converter<?> converter;

        private ParameterInfo(Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
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
            } else if (parameter.getAnnotation(Context.class) != null) {
                type = ParameterType.BODY;
                converter = convertersHolder.findConsumerConverter(contentType);
            }
        }

        private Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
                throws ConverterException {

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
                    return converter.convert(classType, requestData.pathValues().value(name));
                case QUERYPARAM:
                    return converter.convert(classType, req.getParameter(name));
                case BODY:
                    return converter.convert(requestData, classType);
                default:
                    return null;
                }
            } catch (ConverterException ce) {
                throw ce;
            } catch (Exception e) {
                throw new ConverterException("Type convertion problem", e);
            }
        }
    }

    private ComponentProvider componentProvider;

    private Class<?> clazz;

    private Method method;

    private final List<ParameterInfo> parameters = new ArrayList<>();

    public MethodInvoker(Class<?> clazz, Method method, ComponentProvider componentProvider, ConvertersHolder convertersHolder,
            String contentType) {
        this.clazz = clazz;
        this.method = method;
        this.componentProvider = componentProvider;
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isSynthetic()) {
                continue;
            }
            parameters.add(new ParameterInfo(parameter, convertersHolder, contentType));
        }
    }

    public Object invoke(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws ConverterException {
        try {

            Object object = componentProvider.provide(clazz);
            RequestData requestData = new RequestData(req, pathValues, containsBody());

            Object[] values;
            if (parameters.size() == 0) {
                values = new Object[0];
            } else {
                values = new Object[parameters.size()];
            }

            for (int i = 0; i < parameters.size(); i++) {
                values[i] = parameters.get(i).findValue(req, res, con, requestData);
            }

            return method.invoke(object, values);
        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }

    private boolean containsBody() {
        for (ParameterInfo info : parameters) {
            if (info.type == ParameterType.BODY) {
                return true;
            }
        }
        return false;
    }
}
