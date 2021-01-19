package net.cactusthorn.routing;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;

public final class ConsumesParser {

    Set<MediaType> consumes(Class<?> clazz) {
        Consumes consumes = clazz.getAnnotation(Consumes.class);
        if (consumes != null) {
            return parseConsumes(consumes.value());
        }
        Set<MediaType> mediaTypes = new HashSet<>();
        mediaTypes.add(MediaType.WILDCARD_TYPE);
        return Collections.unmodifiableSet(mediaTypes);
    }

    Set<MediaType> consumes(Method method, Set<MediaType> classMediaTypes) {
        Consumes consumes = method.getAnnotation(Consumes.class);
        if (consumes != null) {
            return parseConsumes(consumes.value());
        }
        return classMediaTypes;
    }

    private Set<MediaType> parseConsumes(String[] consumes) {
        Set<MediaType> mediaTypes = new HashSet<>();
        for (String value : consumes) {
            for (String subValue : value.split(",")) {
                String[] parts = subValue.trim().split(";")[0].split("/");
                mediaTypes.add(new MediaType(parts[0], parts[1]));
            }
        }
        return mediaTypes;
    }
}
