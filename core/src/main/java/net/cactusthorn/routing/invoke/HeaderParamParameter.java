package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.HeaderParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class HeaderParamParameter extends PathParamParameter {

    public HeaderParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(method, parameter, convertersHolder);
    }

    protected String annotationName() {
        return HeaderParam.class.getSimpleName();
    }

    protected String initName(Parameter parameter) {
        HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
        return headerParam.value();
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {
            String value = req.getHeader(name());
            if (defaultValue() != null && value == null) {
                value = defaultValue();
            }
            return converter().convert(classType(), value);
        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConverterException("Type Converting failed: header parameter \"%s\"", e, name());
        }
    }

}