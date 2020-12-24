package net.cactusthorn.routing.invoke;

import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class BodyParameter extends MethodParameter {

    private Converter converter;

    public BodyParameter(Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(parameter);
        converter = convertersHolder.findConsumerConverter(contentType);
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        return converter.convert(requestData, classType());
    }

}
