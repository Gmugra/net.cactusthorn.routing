package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;

public final class BodyReaderParameter extends MethodParameter {

    private static final String WRONG_ENTITY_TYPE = "%s body reader do not support type %s; Method: %s";

    private Type parameterGenericType;
    private Map<MediaType, MessageBodyReader<?>> bodyReaders = new HashMap<>();

    public BodyReaderParameter(Method method, Parameter parameter, Type parameterGenericType,
            Map<MediaType, MessageBodyReader<?>> bodyReaders) {

        super(parameter);
        this.parameterGenericType = parameterGenericType;
        this.bodyReaders = bodyReaders;
        for (Map.Entry<MediaType, MessageBodyReader<?>> entity : this.bodyReaders.entrySet()) {
            if (!entity.getValue().isReadable(classType(), parameterGenericType, parameter().getAnnotations(), entity.getKey())) {
                throw new RoutingInitializationException(WRONG_ENTITY_TYPE, entity.getKey(), classType(), method);
            }
        }
    }

    @Override @SuppressWarnings("unchecked") //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        MediaType mediaType = contentType(req);
        @SuppressWarnings("rawtypes") MessageBodyReader bodyReader = bodyReaders.get(contentType(req));
        return bodyReader.readFrom(classType(), parameterGenericType, parameter().getAnnotations(), mediaType, getHeaders(req),
                req.getInputStream());
    }

    private MediaType contentType(HttpServletRequest req) {
        String contenttype = req.getContentType();
        return MediaType.valueOf(contenttype);
    }

    private MultivaluedMap<String, String> getHeaders(HttpServletRequest req) {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        Enumeration<String> names = req.getHeaderNames();
        if (names == null) {
            return map;
        }
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = req.getHeaders(name);
            List<String> list = new ArrayList<>();
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                list.add(value);
            }
            map.put(name, list);
        }

        MediaType mt = MediaType.valueOf(map.getFirst(HttpHeaders.CONTENT_TYPE));
        if (!mt.getParameters().containsKey(MediaType.CHARSET_PARAMETER)) {
            map.putSingle(HttpHeaders.CONTENT_TYPE, mt.withCharset(req.getCharacterEncoding()).toString());
        }

        return map;
    }
}
