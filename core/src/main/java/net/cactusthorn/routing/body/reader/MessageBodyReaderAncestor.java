package net.cactusthorn.routing.body.reader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

public class MessageBodyReaderAncestor {

    protected static final int BUFFER_SIZE = 1024;

    protected String streamToString(InputStream inputStream, MediaType mediaType) throws IOException {
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(charset);
    }
}
