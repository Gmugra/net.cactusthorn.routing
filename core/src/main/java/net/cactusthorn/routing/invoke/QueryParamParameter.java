package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class QueryParamParameter extends MethodParameter {

    public QueryParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(method, parameter, convertersHolder, contentType);
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        name = queryParam.value();
        converterType = converterType(method, classType);
        converter = convertersHolder.findConverter(converterType);
    }

    @Override //
    final Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {

            // not array or collection
            if (classType == converterType) {
                return converter.convert(converterType, req.getParameter(name));
            }

            // at the moment only arrays supported
            return converter.convert(converterType, req.getParameterValues(name));
            
            //TODO collections support

        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConverterException("Type Converting problem", e);
        }
    }
}
