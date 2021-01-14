package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Link;

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

    @Test public void linkUnsupported() {
        Link link = Link.valueOf("<example.com>");
        assertThrows(UnsupportedOperationException.class, () -> link.getUriBuilder());
    }

    @Test public void link() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.uri("example.com").build();
        Link link2 = builder.link(link).build();
        assertEquals(link.toString(), link2.toString());
        assertNotEquals(link.hashCode(), link2.hashCode());
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

    @Test public void title() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        Link link = builder.link("<https://example.com>; title=compact").build();
        assertEquals("compact", link.getTitle());
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

    @Test public void builderUnsupported() {
        Link.Builder builder = new LinkImpl.LinkBuilderImpl();
        assertThrows(UnsupportedOperationException.class, () -> builder.baseUri((URI) null));
        assertThrows(UnsupportedOperationException.class, () -> builder.baseUri((String) null));
        assertThrows(UnsupportedOperationException.class, () -> builder.uriBuilder(null));
        assertThrows(UnsupportedOperationException.class, () -> builder.buildRelativized(null));
    }

}
