package net.cactusthorn.routing.delegate.uribuilder;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.UriBuilderImpl;

public class UriBuilderSimpleTest {

    @Test public void builderClone() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("//user@cactusthorn.net:8080");
        UriBuilder cloned = builder.clone();
        assertNotEquals(builder.hashCode(), cloned.hashCode());
        assertEquals(builder.build(), cloned.build());
    }

    @Test public void matrix() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(UnsupportedOperationException.class, () -> builder.replaceMatrix(null));
        assertThrows(UnsupportedOperationException.class, () -> builder.matrixParam(null));
        assertThrows(UnsupportedOperationException.class, () -> builder.replaceMatrixParam(null));
    }

    @Test public void uriNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.uri((String) null));
        assertThrows(IllegalArgumentException.class, () -> builder.uri((URI) null));
    }

    @Test public void uri() throws URISyntaxException {
        URI expected = new URI("http", "user", "host", 10, "/aaa", "bb=cc", "fragment");
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("https://userInfo@/ccc");
        builder.uri(expected);
        assertEquals(expected, builder.build());
    }

    @Test public void uri2() throws URISyntaxException {
        URI source = new URI(null, "user", "host", 10, "/aaa", "", null);
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("https://userInfo@/ccc#abc");
        builder.uri(source);
        assertEquals("https://user@host:10/aaa#abc", builder.build().toString());
    }

    @Test public void uri3() throws URISyntaxException {
        URI source = new URI("aaa");
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("https://userInfo@/ccc#abc");
        builder.uri(source);
        assertEquals("https://userInfo@/aaa#abc", builder.build().toString());
    }

    @Test public void uriOpaque() throws URISyntaxException {
        URI source = new URI("mailto", "a@a.com", null);
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("https://userInfo@/ccc#abc");
        builder.uri(source);
        assertEquals("mailto:a@a.com#abc", builder.build().toString());
    }

    @Test public void uriAuthority() throws URISyntaxException {
        URI source = new URI(null, "@", null, null, null);
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("https://userInfo@/ccc#abc");
        builder.uri(source);
        assertEquals("https://@/ccc#abc", builder.build().toString());
    }

    @Test public void uriPath() throws URISyntaxException {
        URI source = new URI(null, null, "abc", "aa=bb", null);
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/ccc#abc");
        builder.uri(source);
        assertEquals("abc?aa=bb#abc", builder.build().toString());
    }

    @Test public void schemeHost() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.scheme("http").host("cactusthorn.net").build();
        assertEquals("http", uri.getScheme());
    }

    @Test public void sspOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.scheme("mailto").schemeSpecificPart("java-net@java.sun.com").build();
        assertEquals("java-net@java.sun.com", uri.getSchemeSpecificPart());
    }

    @Test public void sspHierarchical() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.schemeSpecificPart("//user@cactusthorn.net:8080").build();
        assertEquals("user", uri.getUserInfo());
        assertEquals("cactusthorn.net", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("", uri.getPath());
    }

    @Test public void sspHierarchical2() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.schemeSpecificPart("//@/abc").build();
        assertEquals("@", uri.getAuthority());
        assertNull(uri.getUserInfo());
        assertNull(uri.getHost());
        assertEquals(-1, uri.getPort());
        assertEquals("/abc", uri.getPath());
    }

    @Test public void sspNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.schemeSpecificPart(null));
    }

    @Test public void sspFragment() {
        UriBuilder builder = new UriBuilderImpl();
        builder.scheme("mailto");
        assertThrows(IllegalArgumentException.class, () -> builder.schemeSpecificPart("java-net@java.sun.com#abc"));
    }

    @Test public void userInfoOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com");
        assertThrows(IllegalArgumentException.class, () -> builder.userInfo("user"));
    }

    @Test public void userInfo() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("/path1/path2").userInfo("user").host("cactusthorn.net").build();
        assertEquals("//user@cactusthorn.net/path1/path2", uri.toString());
        uri = builder.userInfo(null).build();
        assertEquals("//cactusthorn.net/path1/path2", uri.toString());
    }

    @Test public void hostOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com");
        assertThrows(IllegalArgumentException.class, () -> builder.host("cactusthorn.net"));
    }

    @Test public void host() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("/path1/path2").host("cactusthorn.net").build();
        assertEquals("//cactusthorn.net/path1/path2", uri.toString());
        uri = builder.host(null).build();
        assertEquals("/path1/path2", uri.toString());
    }

    @Test public void portOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com");
        assertThrows(IllegalArgumentException.class, () -> builder.port(8080));
    }

    @Test public void portWrong() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.port(-10));
    }

    @Test public void port() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.host("cactusthorn.net").port(8080).build();
        assertEquals("//cactusthorn.net:8080", uri.toString());
        uri = builder.port(-1).build();
        assertEquals("//cactusthorn.net", uri.toString());
    }

    @Test public void fragment() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailtop:a@a.com#abc").fragment("ww rrr");
        assertEquals("mailtop:a@a.com#ww%20rrr", builder.build().toString());
    }

    @Test public void fragmentDelete() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailtop:a@a.com#abc").fragment(null);
        assertEquals("mailtop:a@a.com", builder.build().toString());
    }

    @Test public void autority() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = new URI("http://@/path");
        builder.uri(uri);
        assertEquals("http://@/path", builder.build().toString());
    }

    @Test public void autority2() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = new URI("http://@:80/path");
        builder.uri(uri);
        assertEquals("http://@:80/path", builder.build().toString());
    }

    @Test public void schemeNull() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("http://a.com").scheme(null);
        assertEquals("//a.com", builder.build().toString());
    }
}
