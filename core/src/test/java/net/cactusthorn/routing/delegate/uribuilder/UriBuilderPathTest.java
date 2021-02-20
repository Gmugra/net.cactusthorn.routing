package net.cactusthorn.routing.delegate.uribuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.UriBuilderImpl;

public class UriBuilderPathTest {

    public static class NotResource {
    }

    @Path("xyz") public static class Resource {
        public void xx() {
        }

        @Path("abc") public void zzz() {
        }

        @Path("abc2") public void ttt() {
        }

        @Path("abc3") public void ttt(int i) {
        }
    }

    @Test public void pathOpaque() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com");
        assertThrows(IllegalArgumentException.class, () -> builder.replacePath("aa"));
        assertThrows(IllegalArgumentException.class, () -> builder.path("aa"));
        assertThrows(IllegalArgumentException.class, () -> builder.path(Resource.class));
        assertThrows(IllegalArgumentException.class, () -> builder.path(Resource.class, "zzz"));
        assertThrows(IllegalArgumentException.class, () -> builder.path(findMethod(Resource.class, "zzz")));
        assertThrows(IllegalArgumentException.class, () -> builder.segment("aaaaa"));
    }

    @Test public void replacePath() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc?aa=bb").replacePath(null);
        assertEquals("?aa=bb", builder.build().toString());
        assertEquals("/aa/ccc/ddd?aa=bb", builder.replacePath("/aa/ccc/ddd").build().toString());
    }

    @Test public void appendPath() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com").path("xxxxx").build();
        assertEquals("/xxxxx", uri.getPath());
    }

    @Test public void appendPath2() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com/").path("xxxxx").build();
        assertEquals("/xxxxx", uri.getPath());
    }

    @Test public void appendPath3() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com/aa/").path("/xxxxx").build();
        assertEquals("/aa/xxxxx", uri.getPath());
    }

    @Test public void appendPath4() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com/aa/").path("xxxxx").build();
        assertEquals("/aa/xxxxx", uri.getPath());
    }

    @Test public void appendPath5() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com/aa").path("/xxxxx").build();
        assertEquals("/aa/xxxxx", uri.getPath());
    }

    @Test public void appendPathMulti() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com").path("aa/bb").path("cc").build();
        assertEquals("/aa/bb/cc", uri.getPath());
    }

    @Test public void appendPathNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path((String) null));
    }

    @Test public void appendPathClass() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.uri("http://java-net@java.sun.com").path(Resource.class).build();
        assertEquals("/xyz", uri.getPath());
    }

    @Test public void appendPathClassNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path((Class<?>) null));
    }

    @Test public void appendPathClassNotResource() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path(NotResource.class));
    }

    @Test public void appendPathClassMethodNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path(null, "yy"));
        assertThrows(IllegalArgumentException.class, () -> builder.path(NotResource.class, null));
    }

    @Test public void appendPathClassMethodNotExists() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path(Resource.class, "yyy"));
        assertThrows(IllegalArgumentException.class, () -> builder.path(Resource.class, "xx"));
    }

    @Test public void appendPathClassMethod() {
        UriBuilder builder = new UriBuilderImpl();
        URI uri = builder.path(Resource.class, "zzz").build();
        assertEquals("abc", uri.getPath());
    }

    @Test public void appendPathClassMethodMultiple() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path(Resource.class, "ttt").build());
    }

    @Test public void appendPathMethodNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path((Method) null));
    }

    @Test public void appendPathMethodNotExists() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.path(findMethod(Resource.class, "xx")));
    }

    @Test public void appendPathMethod() {
        UriBuilder builder = new UriBuilderImpl();
        Method method = findMethod(Resource.class, "zzz");
        URI uri = builder.path(method).build();
        assertEquals("abc", uri.getPath());
    }

    @Test public void segments() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("/abc").segment("aaa", "a/b/c", "wwww");
        assertEquals("/abc/aaa/a%2Fb%2Fc/wwww", builder.build().toString());
    }

    @Test public void segmentsNull() throws URISyntaxException {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.segment((String[]) null));
        assertThrows(IllegalArgumentException.class, () -> builder.segment("aaaa", null, "bbbb"));
    }

    protected Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
