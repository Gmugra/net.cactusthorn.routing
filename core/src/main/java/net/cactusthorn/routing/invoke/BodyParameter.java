package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConsumerConverter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class BodyParameter extends MethodParameter {

    private ConsumerConverter converter;

    public BodyParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(parameter);
        Optional<ConsumerConverter> optional = convertersHolder.findConsumerConverter(contentType);
        if (!optional.isPresent()) {
            throw new RoutingInitializationException(
                    "@Context: consumer for contentType %s unknown; Method: %s", contentType, method);
        }
        converter = optional.get();
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        return converter.convert(requestData, classType());
    }

}
