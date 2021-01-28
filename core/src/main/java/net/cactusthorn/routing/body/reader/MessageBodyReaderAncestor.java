package net.cactusthorn.routing.body.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;

public abstract class MessageBodyReaderAncestor<T> implements InitializableMessageBodyReader<T> {

    private int ioBufferSize;

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        ioBufferSize = (int) routingConfig.properties().get(ConfigProperty.IO_BUFFER_SIZE);
    }

    protected String streamToString(InputStream inputStream, MediaType mediaType) throws IOException {
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[ioBufferSize];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(charset);
    }
}
