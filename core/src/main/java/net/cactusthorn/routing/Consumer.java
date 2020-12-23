package net.cactusthorn.routing;

public interface Consumer {

    Object consume(Class<?> clazz, String mediaType, RequestData data);
}
