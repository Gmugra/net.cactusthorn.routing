package net.cactusthorn.routing.bodywriter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;

public class WildcardMessageBodyWriterTest {

    @Test //
    public void isWriteable() {
        WildcardMessageBodyWriter writer = new WildcardMessageBodyWriter();
        assertTrue(writer.isWriteable(null, null, null, null));
    }

    @Test //
    public void writeToWildcard() throws WebApplicationException, IOException {
        WildcardMessageBodyWriter writer = new WildcardMessageBodyWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();

        writer.writeTo(22, null, null, null, MediaType.WILDCARD_TYPE.withCharset("KOI8-R"), httpHeaders, out);
        MediaType mediaType = (MediaType) httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
        assertEquals("KOI8-R", mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
        assertEquals(MediaType.TEXT_PLAIN_TYPE.getType(), mediaType.getType());
        assertEquals(MediaType.TEXT_PLAIN_TYPE.getSubtype(), mediaType.getSubtype());
        assertEquals("22", new String(out.toByteArray()));
    }

    @Test public void writeToJson() throws WebApplicationException, IOException {
        WildcardMessageBodyWriter writer = new WildcardMessageBodyWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();

        writer.writeTo(22, null, null, null, MediaType.APPLICATION_JSON_TYPE.withCharset("KOI8-R"), httpHeaders, out);
        MediaType mediaType = (MediaType) httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
        assertEquals("KOI8-R", mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
        assertEquals(MediaType.APPLICATION_JSON_TYPE.getType(), mediaType.getType());
        assertEquals(MediaType.APPLICATION_JSON_TYPE.getSubtype(), mediaType.getSubtype());
        assertEquals("22", new String(out.toByteArray()));
    }
}
