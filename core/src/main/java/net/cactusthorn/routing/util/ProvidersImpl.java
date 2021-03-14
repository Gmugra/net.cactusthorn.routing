package net.cactusthorn.routing.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.reader.BodyReader;
import net.cactusthorn.routing.body.writer.BodyWriter;

public class ProvidersImpl implements Providers {

    private final List<BodyReader> bodyReaders;
    private final List<BodyWriter> bodyWriters;
    private final List<ExceptionMapperWrapper<? extends Throwable>> exceptionMappers;

    public ProvidersImpl(List<BodyReader> bodyReaders, List<BodyWriter> bodyWriters,
            List<ExceptionMapperWrapper<? extends Throwable>> exceptionMappers) {
        this.bodyReaders = bodyReaders;
        this.bodyWriters = bodyWriters;
        this.exceptionMappers = exceptionMappers;
        Collections.sort(this.bodyReaders, Prioritised.PRIORITY_COMPARATOR);
        Collections.sort(this.bodyWriters, Prioritised.PRIORITY_COMPARATOR);
        Collections.sort(this.exceptionMappers, Prioritised.PRIORITY_COMPARATOR);
    }

    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        bodyWriters.forEach(w -> w.init(servletContext, routingConfig));
        bodyReaders.forEach(r -> r.init(servletContext, routingConfig));
    }

    @Override @SuppressWarnings("unchecked") //
    public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        for (BodyReader bodyReader : bodyReaders) {
            if (bodyReader.isProcessable(type, genericType, annotations, mediaType)) {
                return (MessageBodyReader<T>) bodyReader.messageBodyReader();
            }
        }
        return null;
    }

    @Override @SuppressWarnings("unchecked") //
    public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        for (BodyWriter bodyWriter : bodyWriters) {
            if (bodyWriter.isProcessable(type, genericType, annotations, mediaType)) {
                return (MessageBodyWriter<T>) bodyWriter.messageBodyWriter();
            }
        }
        return null;
    }

    @Override @SuppressWarnings("unchecked") //
    public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
        for (ExceptionMapperWrapper<? extends Throwable> wrapper : exceptionMappers) {
            if (wrapper.throwable() == type) {
                return (ExceptionMapperWrapper<T>) wrapper;
            }
        }
        return null;
    }

    @Override //
    public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType) {
        throw new UnsupportedOperationException();
    }
}
