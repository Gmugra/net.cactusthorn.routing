package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

public class LinkTest {

    @Test public void all() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        builder.uri("https://example.com");
        builder.rel("preconnect");
        builder.type("text/css");
        builder.title("compact");
        builder.param("aaa", "bb cc");

        Link link = builder.build();
        assertEquals("<https://example.com>; rel=preconnect; type=text/css; title=compact; aaa=\"bb cc\"", link.toString());
    }

    @Test public void paramNull() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.param(null, "aa"));
        assertThrows(IllegalArgumentException.class, () -> builder.param("aa", null));
    }

    @Test public void getUriBuilder() {
        Link link = Link.valueOf("<http://example.com>");
        UriBuilder uriBuilder = link.getUriBuilder();
        assertEquals("http://example.com", uriBuilder.build().toString());
    }

    @Test public void uriBuilder() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        builder.uriBuilder(UriBuilder.fromUri("http://example.com"));
        assertEquals("<http://example.com>", builder.build().toString());
    }

    @Test public void link() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.uri("http://example.com").build();
        Link link2 = builder.link(link).build();
        assertEquals(link.toString(), link2.toString());
        assertNotEquals(link.hashCode(), link2.hashCode());
    }

    @Test public void relNull() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.rel(null));
    }

    @Test public void rel() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.link("<https://example.com>; rel=\"preconnect\"").build();
        assertEquals("preconnect", link.getRel());
    }

    @Test public void rels() throws URISyntaxException {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.link("  <  https://example.com  >; rel=\"aa bb cc\"  ").build();
        assertEquals(3, link.getRels().size());
        assertEquals("<https://example.com>; rel=\"aa bb cc\"", link.toString());

        builder = new LinkImpl.LinkBuilderImpl();
        link = builder.uri(new URI("https://example.com")).build();
        assertTrue(link.getRels().isEmpty());
    }

    @Test public void titleNull() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.title(null));
    }

    @Test public void title() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.link("<https://example.com>; title=compact").build();
        assertEquals("compact", link.getTitle());
    }

    @Test public void typeNull() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.type(null));
    }

    @Test public void type() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.link("<https://example.com>; type=text/css").build();
        assertEquals("text/css", link.getType());
    }

    @Test public void wrong() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.link("https://example.com>; type=text/css"));
        assertThrows(IllegalArgumentException.class, () -> builder.link("<https://example.com; type=text/css"));
        assertThrows(IllegalArgumentException.class, () -> builder.link("<https://example.com>; type-text/css"));
    }

    @Test public void template() {
        Link link = Link.fromUri("http://{host}/root/customers/{id}").rel("update").type("text/plain").build("localhost", "1234");
        assertEquals("<http://localhost/root/customers/1234>; rel=update; type=text/plain", link.toString());
    }

    @Test public void buildRelativized() throws URISyntaxException {
        Link link = Link.fromUri("a/d/e").rel("update").type("text/plain").baseUri("http://localhost/")
                .buildRelativized(new URI("http://localhost/a"));
        assertEquals("<d/e>; rel=update; type=text/plain", link.toString());
    }

    @Test public void buildRelativized2() throws URISyntaxException {
        Link link = Link.fromUri("a/d/e").rel("update").type("text/plain").buildRelativized(new URI("a"));
        assertEquals("<d/e>; rel=update; type=text/plain", link.toString());
    }

    @Test public void buildRelativized3() throws URISyntaxException {
        Link link = Link.fromUri("a/d/e").rel("update").type("text/plain").baseUri(new URI("http://localhost/"))
                .buildRelativized(new URI("http://localhost/a"));
        assertEquals("<d/e>; rel=update; type=text/plain", link.toString());
    }

    @Test public void buildRelativizedNull() throws URISyntaxException {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.buildRelativized(null));
    }
}
