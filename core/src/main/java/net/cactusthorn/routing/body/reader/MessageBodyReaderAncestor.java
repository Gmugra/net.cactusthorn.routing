package net.cactusthorn.routing.body.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.ws.rs.core.MediaType;

public class MessageBodyReaderAncestor {

    protected String streamToString(InputStream inputStream, MediaType mediaType) throws IOException {
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }
}
