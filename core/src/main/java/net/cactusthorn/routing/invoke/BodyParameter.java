package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConsumerConverter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class BodyParameter extends MethodParameter {

    private Map<MediaType, ConsumerConverter> consumerConverters = new HashMap<>();

    public BodyParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, Set<MediaType> consumesMediaTypes) {
        super(parameter);
        for (MediaType contentType : consumesMediaTypes) {
            Optional<ConsumerConverter> optional = convertersHolder.findConsumerConverter(contentType);
            if (!optional.isPresent()) {
                throw new RoutingInitializationException("@Context: consumer for contentType %s unknown; Method: %s", contentType, method);
            }
            consumerConverters.put(contentType, optional.get());
        }
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        ConsumerConverter converter = consumerConverters.get(contentType(req));
        if (converter != null) {
            return converter.convert(requestData, classType());
        }
        return null;
    }

    private MediaType contentType(HttpServletRequest req) {
        String consumes = req.getContentType();
        if (consumes == null || consumes.trim().isEmpty()) {
            return MediaType.WILDCARD_TYPE;
        }
        return MediaType.valueOf(consumes);
    }

}
