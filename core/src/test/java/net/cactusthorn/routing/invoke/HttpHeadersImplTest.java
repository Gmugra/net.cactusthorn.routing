package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HttpHeadersImplTest extends InvokeTestAncestor {

    @Test public void getRequestHeader() {
        HttpHeaders headers = httpHeaders("test-header", "test-value");
        assertEquals("test-value", headers.getRequestHeader("test-header").get(0));
    }

    @Test public void getRequestHeaders() {
        HttpHeaders headers = httpHeaders("test-header", "test-value");
        MultivaluedMap<String, String> result = headers.getRequestHeaders();
        assertEquals("test-value", result.getFirst("test-header"));
    }

    @Test public void getHeaderString() {
        HttpHeaders headers = httpHeaders("test-header", "test-value");
        assertEquals("test-value", headers.getHeaderString("test-header"));
        assertNull(headers.getHeaderString("test-header2"));
    }

    @Test public void nullHeaders() {
        Mockito.when(request.getHeaderNames()).thenReturn(null);
        HttpHeaders headers = new HttpHeadersParameter.HttpHeadersImpl(request);
        assertNotNull(headers);
    }

    @Test public void emptyHeaders() {
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        HttpHeaders headers = new HttpHeadersParameter.HttpHeadersImpl(request);
        assertNotNull(headers);
    }

    @Test public void getAcceptableMediaTypes() {
        HttpHeaders headers = httpHeaders(HttpHeaders.ACCEPT,
                "image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        List<MediaType> types = headers.getAcceptableMediaTypes();
        assertEquals(5, types.size());
        assertEquals(MediaType.valueOf("application/signed-exchange;v=b3;q=0.9"), types.get(3));
    }

    @Test public void getAcceptableLanguages() {
        HttpHeaders headers = httpHeaders(HttpHeaders.ACCEPT_LANGUAGE, "fr-CH, en;q=0.8, de;q=0.7, *;q=0.5, fr;q=0.9");
        List<Locale> locales = headers.getAcceptableLanguages();
        assertEquals(5, locales.size());
        assertEquals(new Locale("fr"), locales.get(1));
    }

    @Test public void getMediaType() {
        HttpHeaders headers = httpHeaders(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        MediaType type = headers.getMediaType();
        assertEquals(MediaType.APPLICATION_JSON_TYPE, type);
    }

    @Test public void getCookies() {
        HttpHeaders headers = httpHeaders(HttpHeaders.COOKIE, "aa=bbb, cc=ddd");
        Map<String, Cookie> cookies = headers.getCookies();
        assertEquals("bbb", cookies.get("aa").getValue());
        assertEquals("ddd", cookies.get("cc").getValue());
    }

    @Test public void getDate() {
        HttpHeaders headers = httpHeaders(HttpHeaders.DATE, "Thu, 01 Dec 1994 16:00:00 GMT");
        assertNotNull(headers.getDate());
    }

    @Test public void getLanguage() {
        HttpHeaders headers = httpHeaders(HttpHeaders.CONTENT_LANGUAGE, "fr-CH");
        assertEquals(new Locale("fr", "CH"), headers.getLanguage());
    }

    @Test public void getNullLength() {
        HttpHeaders headers = httpHeaders(HttpHeaders.CONTENT_LANGUAGE, "fr-CH");
        assertEquals(-1, headers.getLength());
    }

    @Test public void getLength() {
        HttpHeaders headers = httpHeaders(HttpHeaders.CONTENT_LENGTH, "100");
        assertEquals(100, headers.getLength());
    }

    private HttpHeaders httpHeaders(String name, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(name, value);
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(map.keySet()));
        Mockito.when(request.getHeaders(name)).thenReturn(Collections.enumeration(map.values()));

        return new HttpHeadersParameter.HttpHeadersImpl(request);
    }
}
