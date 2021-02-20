package net.cactusthorn.routing.delegate.uribuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.UriBuilderImpl;

public class UriBuilderQueryTest {

    @Test public void pathOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com");
        assertThrows(IllegalArgumentException.class, () -> builder.replaceQuery("aa=bb"));
        assertThrows(IllegalArgumentException.class, () -> builder.queryParam("aa", "bb"));
        assertThrows(IllegalArgumentException.class, () -> builder.replaceQueryParam("aa", "bb"));
    }

    @Test public void replaceQuery() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?aa=bb").replaceQuery("z=c&b=a");
        assertEquals("/abc?z=c&b=a", builder.build().toString());
    }

    @Test public void replaceQueryNull() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?aa=bb").replaceQuery(null);
        assertEquals("/abc", builder.build().toString());
    }

    @Test public void queryParamNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.queryParam("aaa", (Object[]) null));
        assertThrows(IllegalArgumentException.class, () -> builder.queryParam(null, "aaaa"));
        assertThrows(IllegalArgumentException.class, () -> builder.replaceQueryParam(null, "aaaa"));
    }

    @Test public void queryParamEmpty() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?aa=bb").queryParam("bbbb", new Object[0]);
        assertEquals("/abc?aa=bb", builder.build().toString());
    }

    @Test public void queryParamNew() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc").queryParam("bb", 10, "aaaa", null, 20);
        assertEquals("/abc?bb=10&bb=aaaa&bb=20", builder.build().toString());
    }

    @Test public void queryParam() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?ww=zz").queryParam("bb", 10, "aaaa", null, 20);
        assertEquals("/abc?ww=zz&bb=10&bb=aaaa&bb=20", builder.build().toString());
    }

    @Test public void queryParamAllNull() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?ww=zz").queryParam("bb", new Object[] { null, null });
        assertEquals("/abc?ww=zz", builder.build().toString());
    }

    @Test public void replaceQueryParam() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?ww=zz&tt=aÜb").replaceQueryParam("ww", new Object[] { "ab", "tt tt" });
        assertEquals("/abc?tt=a%C3%9Cb&ww=ab&ww=tt%20tt", builder.build().toString());
    }

    @Test public void replaceQueryParamDelete() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?w w=zz&tt=aÜb").replaceQueryParam("w w", (Object[]) null);
        assertEquals("/abc?tt=a%C3%9Cb", builder.build().toString());
    }

    @Test public void replaceQueryParamDelete2() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?w w=zz&tt=aÜb").replaceQueryParam("w w", new Object[0]);
        assertEquals("/abc?tt=a%C3%9Cb", builder.build().toString());
    }
}
