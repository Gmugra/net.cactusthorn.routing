package net.cactusthorn.routing.body.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.util.Http;

public class MessageBodyHeadersWriter implements MessageBodyWriter<Object> {

    private HttpServletResponse response;
    private MessageBodyWriter<Object> wrapped;

    @SuppressWarnings("unchecked") //
    public MessageBodyHeadersWriter(HttpServletResponse response, MessageBodyWriter<?> writer) {
        this.response = response;
        this.wrapped = (MessageBodyWriter<Object>) writer;
    }

    @Override //
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return wrapped.isWriteable(type, genericType, annotations, mediaType);
    }

    @Override //
    public long getSize(Object entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return wrapped.getSize(entity, type, genericType, annotations, mediaType);
    }

    @Override //
    public void writeTo(Object entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        wrapped.writeTo(entity, type, genericType, annotations, mediaType, httpHeaders,
                new HeadersWriterOutputStream(entityStream, response, httpHeaders));
    }

    private static class HeadersWriterOutputStream extends FilterOutputStream {

        private boolean done;
        private HttpServletResponse response;
        private MultivaluedMap<String, Object> httpHeaders;

        HeadersWriterOutputStream(OutputStream out, HttpServletResponse response, MultivaluedMap<String, Object> httpHeaders) {
            super(out);
            this.response = response;
            this.httpHeaders = httpHeaders;
        }

        private void writeHeaders() {
            if (done) {
                return;
            }
            done = true;
            if (httpHeaders == null) {
                return;
            }
            Http.writeHeaders(response, httpHeaders);
        }

        @Override //
        public void write(int b) throws IOException {
            writeHeaders();
            super.write(b);
        }

        @Override //
        public void write(byte[] b) throws IOException {
            writeHeaders();
            super.write(b);
        }

        @Override //
        public void write(byte[] b, int off, int len) throws IOException {
            writeHeaders();
            super.write(b, off, len);
        }

        @Override //
        public void flush() throws IOException {
            writeHeaders();
            super.flush();
        }

        @Override //
        public void close() throws IOException {
            writeHeaders();
            super.close();
        }
    }
}
