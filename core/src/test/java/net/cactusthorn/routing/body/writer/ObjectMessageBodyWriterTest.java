package net.cactusthorn.routing.body.writer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

public class ObjectMessageBodyWriterTest {

    @Test //
    public void isWriteable() {
        ObjectMessageBodyWriter writer = new ObjectMessageBodyWriter();
        assertTrue(writer.isWriteable(null, null, null, null));
    }

    @Test //
    public void writeToWildcard() throws WebApplicationException, IOException {
        ObjectMessageBodyWriter writer = new ObjectMessageBodyWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        writer.writeTo(22, null, null, null, MediaType.WILDCARD_TYPE.withCharset("KOI8-R"), null, out);

        assertEquals("22", new String(out.toByteArray()));
    }

    @Test public void writeToJson() throws WebApplicationException, IOException {
        ObjectMessageBodyWriter writer = new ObjectMessageBodyWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        writer.writeTo(22, null, null, null, MediaType.APPLICATION_JSON_TYPE.withCharset("KOI8-R"), null, out);
        assertEquals("22", new String(out.toByteArray()));
    }
}
