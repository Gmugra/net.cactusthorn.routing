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
import net.cactusthorn.routing.body.reader.BodyReader;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.BODY_READER_NOT_FOUND;
import static net.cactusthorn.routing.util.Messages.Key.SOMETHING_TOTALLY_WRONG;

public final class BodyReaderParameter extends MethodParameter {

    private Map<MediaType, MessageBodyReader<?>> messageBodyReaders = new HashMap<>();

    public BodyReaderParameter(Method method, Parameter parameter, Type genericType, int position, Set<MediaType> consumesMediaTypes,
            List<BodyReader> bodyReaders) {
        super(method, parameter, genericType, position);

        for (MediaType consumesMediaType : consumesMediaTypes) {
            for (BodyReader bodyReader : bodyReaders) {
                if (bodyReader.isProcessable(type(), genericType(), annotations(), consumesMediaType)) {
                    messageBodyReaders.put(consumesMediaType, bodyReader.messageBodyReader());
                    break;
                }
            }
            if (!messageBodyReaders.containsKey(consumesMediaType)) {
                throw new RoutingInitializationException(Messages.msg(BODY_READER_NOT_FOUND, consumesMediaType, method()));
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        MediaType mediaType = contentType(req);
        MessageBodyReader bodyReader = findBodyReader(req);
        MediaType mediaTypeWithCharset = mediaType.withCharset(req.getCharacterEncoding());
        return bodyReader.readFrom(type(), genericType(), annotations(), mediaTypeWithCharset, getHeaders(req), req.getInputStream());
    }

    private MessageBodyReader<?> findBodyReader(HttpServletRequest req) {
        MediaType requestMediaType = contentType(req);
        for (Map.Entry<MediaType, MessageBodyReader<?>> entry : messageBodyReaders.entrySet()) {
            if (entry.getKey().isCompatible(requestMediaType)) {
                return entry.getValue();
            }
        }
        throw new BadRequestException(Messages.msg(SOMETHING_TOTALLY_WRONG));
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
