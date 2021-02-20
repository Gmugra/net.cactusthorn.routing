package net.cactusthorn.routing.delegate.uribuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.util.HashMap;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.UriBuilderImpl;

public class UriBuilderBuildTest {

    @Test public void buildFromMapNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.buildFromMap(null));
        assertThrows(IllegalArgumentException.class, () -> builder.buildFromMap(null, true));
        assertThrows(IllegalArgumentException.class, () -> builder.buildFromEncodedMap(null));
    }

    @Test public void buildEmpty() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("http://a.com");
        assertEquals("http://a.com", builder.build(new Object[0]).toString());
        assertEquals("http://a.com", builder.build().toString());
        assertEquals("http://a.com", builder.build(new Object[0], true).toString());
        assertEquals("http://a.com", builder.buildFromEncoded(new Object[0]).toString());
    }

    @Test public void buildURIException() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("htt p://a.com");
        assertThrows(UriBuilderException.class, () -> builder.build((Object[]) null));
        assertThrows(UriBuilderException.class, () -> builder.build(null, true));
        assertThrows(UriBuilderException.class, () -> builder.buildFromEncoded((Object[]) null));
    }

    @Test public void buildMapURIException() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("htt p://a.com");
        assertThrows(UriBuilderException.class, () -> builder.buildFromMap(new HashMap<>()));
        assertThrows(UriBuilderException.class, () -> builder.buildFromMap(new HashMap<>(), true));
        assertThrows(UriBuilderException.class, () -> builder.buildFromEncodedMap(new HashMap<>()));
    }

    @Test public void build() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{schema}://{userinfo}@{host}:{port}/{path}?{p}={v}&{p}={port}#{f}");
        URI uri = builder.build("http", "aa:bb", "a.com", 8080, "xyz", "var", "value", "fragment");
        assertEquals("http://aa:bb@a.com:8080/xyz?var=value&var=8080#fragment", uri.toString());
        uri = builder.build("https", "aa2:bb2", "a.com", 8080, "xyz", "var", "val%20ue", "fragment");
        assertEquals("https://aa2:bb2@a.com:8080/xyz?var=val%2520ue&var=8080#fragment", uri.toString());
    }

    @Test public void buildSlash() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{schema}://{userinfo}@{host}:{port}/{path}?{p}={v}&{p}={port}#{f}");
        Object[] vars = new Object[] { "http", "aa:bb", "a.com", 8080, "xyz/ABC", "var", "value", "fragment" };
        URI uri = builder.build(vars, true);
        assertEquals("http://aa:bb@a.com:8080/xyz%2FABC?var=value&var=8080#fragment", uri.toString());
    }

    @Test public void buildFromEncoded() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{schema}://{userinfo}@{host}:{port}/{path}?{p}={v}&{p}={port}#{f}");
        URI uri = builder.buildFromEncoded("https", "aa@2:bb2", "a.com", 8080, "xyz", "var", "val%20ue", "fragment");
        assertEquals("https://aa%402:bb2@a.com:8080/xyz?var=val%20ue&var=8080#fragment", uri.toString());
    }

    @Test public void buildNotEnoughValues() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{schema}://{userinfo}@{host}:{port}/{path}?{p}={v}&{p}={port}#{f}");
        assertThrows(IllegalArgumentException.class, () -> builder.build("http", "aa:bb", "a.com", 8080, "xyz", "var", "value"));
    }

    @Test public void buildWithNullValue() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{schema}://{userinfo}@{host}:{port}/{path}?{p}={v}&{p}={port}#{f}");
        assertThrows(IllegalArgumentException.class, () -> builder.build("http", null, "a.com"));
    }
}
