package net.cactusthorn.routing.body.reader;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

public class StringMessageBodyReaderTest {

    @Test //
    public void exception() throws WebApplicationException, IOException {
        StringMessageBodyReader reader = new StringMessageBodyReader();
        assertThrows(ProcessingException.class, () -> reader.readFrom(String.class, null, null, null, null, null));
    }

    @Test //
    public void exceptionIO() throws WebApplicationException, IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
        StringMessageBodyReader reader = new StringMessageBodyReader();
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset("RRR-10");
        assertThrows(IOException.class, () -> reader.readFrom(String.class, null, null, mediaType, null, is));
    }
}
