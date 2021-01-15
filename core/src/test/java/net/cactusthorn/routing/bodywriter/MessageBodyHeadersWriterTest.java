package net.cactusthorn.routing.bodywriter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class MessageBodyHeadersWriterTest {

    private static MessageBodyWriter<Integer> TEST_WRITER = new MessageBodyWriter<Integer>() {

        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            if (type.isAssignableFrom(Integer.class)) {
                return true;
            }
            return false;
        }

        @Override public long getSize(Integer t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return 3;
        }

        @Override public void writeTo(Integer t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            byte[] bytes = String.valueOf(t).getBytes();
            entityStream.write(bytes);
            entityStream.flush();
            entityStream.close();
        }
    };

    @Test //
    public void testWriteNoHeader() throws WebApplicationException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessageBodyHeadersWriter wrapper = new MessageBodyHeadersWriter(null, TEST_WRITER);
        wrapper.writeTo(22, null, null, null, null, null, out);
        assertEquals("22", new String(out.toByteArray()));
    }

    @Test //
    public void testWriteToString() throws WebApplicationException, IOException {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();
        httpHeaders.addFirst("notusualclass", new StringBuilder());

        MessageBodyHeadersWriter wrapper = new MessageBodyHeadersWriter(response, TEST_WRITER);

        wrapper.writeTo(22, null, null, null, null, httpHeaders, out);

        ArgumentCaptor<String> captorName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorValue = ArgumentCaptor.forClass(String.class);
        Mockito.verify(response).addHeader(captorName.capture(), captorValue.capture());

        assertEquals("notusualclass", captorName.getValue());
        assertNotNull(captorName.getValue());
        assertEquals("22", new String(out.toByteArray()));
    }

    @Test //
    public void testWriteHeader() throws WebApplicationException, IOException {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();
        httpHeaders.addFirst("testheader", "headervalue");
        httpHeaders.put("nulllist", null);
        httpHeaders.put("nullheader", Arrays.asList(new Object[1]));

        MessageBodyHeadersWriter wrapper = new MessageBodyHeadersWriter(response, TEST_WRITER);

        wrapper.writeTo(22, null, null, null, null, httpHeaders, out);

        ArgumentCaptor<String> captorName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorValue = ArgumentCaptor.forClass(String.class);
        Mockito.verify(response).addHeader(captorName.capture(), captorValue.capture());

        assertEquals("testheader", captorName.getValue());
        assertEquals("headervalue", captorValue.getValue());
        assertEquals("22", new String(out.toByteArray()));
    }

    @Test //
    public void getSize() {
        MessageBodyHeadersWriter wrapper = new MessageBodyHeadersWriter(null, TEST_WRITER);
        assertEquals(3, wrapper.getSize(null, null, null, null, null));
    }

    @Test public void isWriteable() {
        MessageBodyHeadersWriter wrapper = new MessageBodyHeadersWriter(null, TEST_WRITER);
        assertTrue(wrapper.isWriteable(Integer.class, null, null, null));
        assertFalse(wrapper.isWriteable(String.class, null, null, null));
    }

}
