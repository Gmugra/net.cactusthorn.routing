package net.cactusthorn.routing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public final class ProducesParser {

    List<MediaType> produces(Class<?> clazz) {
        Produces produces = clazz.getAnnotation(Produces.class);
        if (produces != null) {
            return parseProduces(produces.value());
        }
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN_TYPE);
        return mediaTypes;
    }

    List<MediaType> produces(Method method, List<MediaType> classMediaTypes) {
        Produces produces = method.getAnnotation(Produces.class);
        if (produces != null) {
            return parseProduces(produces.value());
        }
        return classMediaTypes;
    }

    private List<MediaType> parseProduces(String[] consumes) {
        List<MediaType> mediaTypes = new ArrayList<>();
        for (String value : consumes) {
            for (String subValue : value.split(",")) {
                String[] parts = subValue.trim().split(";")[0].split("/");
                mediaTypes.add(new MediaType(parts[0], parts[1]));
            }
        }
        Collections.sort(mediaTypes, Http.ACCEPT_COMPARATOR);
        return mediaTypes;
    }
}
