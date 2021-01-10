package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class InvokeTestAncestor {

    protected final static ConvertersHolder HOLDER = new ConvertersHolder();

    protected static final Set<MediaType> DEFAULT_CONTENT_TYPES;
    static {
        DEFAULT_CONTENT_TYPES = new HashSet<>();
        DEFAULT_CONTENT_TYPES.add(MediaType.WILDCARD_TYPE);
    }

    protected HttpServletRequest request;

    @BeforeEach //
    protected void setUp() throws Exception {
        request = Mockito.mock(HttpServletRequest.class);
    }

    protected Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    protected static Set<MediaType> mediaTypes(String type, String subtype) {
        Set<MediaType> mediaTypes = new HashSet<>();
        mediaTypes.add(new MediaType(type, subtype));
        return mediaTypes;
    }

    protected static Set<MediaType> mediaTypes(String type, String subtype, String charset) {
        Set<MediaType> mediaTypes = new HashSet<>();
        mediaTypes.add(new MediaType(type, subtype).withCharset(charset));
        return mediaTypes;
    }

    protected static Set<MediaType> mediaTypes(MediaType mediaType) {
        Set<MediaType> mediaTypes = new HashSet<>();
        mediaTypes.add(mediaType);
        return mediaTypes;
    }

    protected static Set<MediaType> mediaTypes(MediaType mediaType, String charset) {
        Set<MediaType> mediaTypes = new HashSet<>();
        mediaTypes.add(mediaType.withCharset(charset));
        return mediaTypes;
    }
}
