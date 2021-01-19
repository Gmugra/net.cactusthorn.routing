package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.body.reader.BodyReader;

public final class BodyReaderParameter extends MethodParameter {

    protected static final String BODY_READER_NOT_FOUND = "body reader for media-type %s not found; Method: %s";

    private Type parameterGenericType;
    private Map<MediaType, MessageBodyReader<?>> messageBodyReaders = new HashMap<>();

    public BodyReaderParameter(Method method, Parameter parameter, Type parameterGenericType, Set<MediaType> consumesMediaTypes,
            List<BodyReader> bodyReaders) {

        super(parameter);
        this.parameterGenericType = parameterGenericType;

        for (MediaType consumesMediaType : consumesMediaTypes) {
            for (BodyReader bodyReader : bodyReaders) {
                if (bodyReader.isProcessable(classType(), parameterGenericType, parameter().getAnnotations(), consumesMediaType)) {
                    messageBodyReaders.put(consumesMediaType, bodyReader.messageBodyReader());
                    break;
                }
            }
            if (!messageBodyReaders.containsKey(consumesMediaType)) {
                throw new RoutingInitializationException(BODY_READER_NOT_FOUND, consumesMediaType, method);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        MediaType mediaType = contentType(req);
        MessageBodyReader bodyReader = findBodyReader(req);
        MediaType mediaTypeWithCharset = mediaType.withCharset(req.getCharacterEncoding());
        return bodyReader.readFrom(classType(), parameterGenericType, parameter().getAnnotations(), mediaTypeWithCharset, getHeaders(req),
                req.getInputStream());
    }

    private MessageBodyReader<?> findBodyReader(HttpServletRequest req) {
        MediaType requestMediaType = contentType(req);
        for (Map.Entry<MediaType, MessageBodyReader<?>> entry : messageBodyReaders.entrySet()) {
            if (entry.getKey().isCompatible(requestMediaType)) {
                return entry.getValue();
            }
        }
        throw new BadRequestException("Something totally wrong");
    }

    private MediaType contentType(HttpServletRequest req) {
        String contenttype = req.getContentType();
        return MediaType.valueOf(contenttype);
    }

    private MultivaluedMap<String, String> getHeaders(HttpServletRequest req) {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        Enumeration<String> names = req.getHeaderNames();
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
        return map;
    }
}
