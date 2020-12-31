package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.Response.Redirect;

public class ResponseTest {

    @Test //
    public void statusCode() {
        Response response = Response.builder().setStatus(300).build();
        assertEquals(300, response.statusCode());
    }

    @Test //
    public void contentType() {
        Response response = Response.builder().setContentType("aa/bb").build();
        assertEquals("aa/bb", response.contentType().get());
    }

    @Test //
    public void characterEncoding() {
        Response response = Response.builder().setCharacterEncoding("KOI8-R").build();
        assertEquals("KOI8-R", response.characterEncoding().get());
    }

    @Test //
    public void body() {
        Response response = Response.builder().setBody("body").build();
        assertEquals("body", response.body());
    }

    @Test //
    public void template() {
        Response response = Response.builder().setTemplate("t").build();
        assertEquals("t", response.template().get());
    }

    @Test //
    public void cookie() {
        Cookie cookie = new Cookie("a", "b");
        Response response = Response.builder().addCookie(cookie).build();
        assertEquals("b", response.cookies().get(0).getValue());
    }

    @Test //
    public void header() {
        Response response = Response.builder().setHeader("a", "b").addHeader("a", "c").build();
        assertArrayEquals(new String[] { "b", "c" }, response.headers().get("a").toArray());
    }

    @Test //
    public void headerAdd() {
        Response response = Response.builder().addHeader("a", "b").addHeader("a", "c").build();
        assertArrayEquals(new String[] { "b", "c" }, response.headers().get("a").toArray());
    }

    @Test //
    public void intHeader() {
        Response response = Response.builder().setIntHeader("a", 100).addIntHeader("a", 200).build();
        assertArrayEquals(new Integer[] { 100, 200 }, response.intHeaders().get("a").toArray());
    }

    @Test //
    public void intHeaderAdd() {
        Response response = Response.builder().addIntHeader("a", 100).addIntHeader("a", 200).build();
        assertArrayEquals(new Integer[] { 100, 200 }, response.intHeaders().get("a").toArray());
    }

    @Test //
    public void dateHeader() {
        Response response = Response.builder().setDateHeader("a", 100L).addDateHeader("a", 200L).build();
        assertArrayEquals(new Long[] { 100L, 200L }, response.dateHeaders().get("a").toArray());
    }

    @Test //
    public void dateHeaderAdd() {
        Response response = Response.builder().addDateHeader("a", 100L).addDateHeader("a", 200L).build();
        assertArrayEquals(new Long[] { 100L, 200L }, response.dateHeaders().get("a").toArray());
    }

    @Test //
    public void skipProducer() {
        Response response = Response.builder().build();
        assertFalse(response.skipProducer());
        response = Response.builder().skipProducer().build();
        assertTrue(response.skipProducer());
    }

    @Test //
    public void seeOther() throws URISyntaxException {
        URI uri = new URI("/xxx");
        Response response = Response.builder().seeOther(uri).build();
        Redirect redirect = response.redirect().get();

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        redirect.apply(resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setHeader(Mockito.eq(Redirect.LOCATION), header.capture());

        assertEquals(HttpServletResponse.SC_SEE_OTHER, code.getValue());
        assertEquals("/xxx", header.getValue());
    }

    @Test //
    public void movedTemporarily() throws URISyntaxException {
        URI uri = new URI("/yyy");
        Response response = Response.builder().movedTemporarily(uri).build();
        Redirect redirect = response.redirect().get();

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        redirect.apply(resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setHeader(Mockito.eq(Redirect.LOCATION), header.capture());

        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, code.getValue());
        assertEquals("/yyy", header.getValue());
    }

    @Test //
    public void movedPermanently() throws URISyntaxException {
        URI uri = new URI("/zzz");
        Response response = Response.builder().movedPermanently(uri).build();
        Redirect redirect = response.redirect().get();

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        redirect.apply(resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setHeader(Mockito.eq(Redirect.LOCATION), header.capture());

        assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, code.getValue());
        assertEquals("/zzz", header.getValue());
    }

    @Test //
    public void redirect() throws URISyntaxException {
        URI uri = new URI("/rrr");
        Response response = Response.builder().redirect(HttpServletResponse.SC_MULTIPLE_CHOICES, uri).build();
        Redirect redirect = response.redirect().get();

        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        redirect.apply(resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setHeader(Mockito.eq(Redirect.LOCATION), header.capture());

        assertEquals(HttpServletResponse.SC_MULTIPLE_CHOICES, code.getValue());
        assertEquals("/rrr", header.getValue());
    }
}
