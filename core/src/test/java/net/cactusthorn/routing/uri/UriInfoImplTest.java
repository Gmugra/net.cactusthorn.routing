package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class UriInfoImplTest {

    private HttpServletRequest request;

    @BeforeEach public void setUp() {
        request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getContextPath()).thenReturn("");
        Mockito.when(request.getServletPath()).thenReturn("");
        Mockito.when(request.getPathInfo()).thenReturn("");
        Mockito.when(request.getHeader("Host")).thenReturn("cactusthorn.net");
        Mockito.when(request.isSecure()).thenReturn(false);
        Mockito.when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());
    }

    @Test public void unsupported() {
        UriInfo info = new UriInfoImpl(request, new PathValues());
        assertThrows(UnsupportedOperationException.class, () -> info.getPathSegments());
        assertThrows(UnsupportedOperationException.class, () -> info.getPathSegments(false));
        assertThrows(UnsupportedOperationException.class, () -> info.getMatchedResources());
        assertThrows(UnsupportedOperationException.class, () -> info.getMatchedURIs());
        assertThrows(UnsupportedOperationException.class, () -> info.getMatchedURIs(false));
    }

    @Test public void baseUriXForwardedProto() {
        Mockito.when(request.getHeader("X-Forwarded-Proto")).thenReturn("https");
        UriInfo info = new UriInfoImpl(request, new PathValues());
        assertEquals("https", info.getBaseUri().getScheme());
    }

    @Test public void baseUriIsSecure() {
        Mockito.when(request.isSecure()).thenReturn(true);
        UriInfo info = new UriInfoImpl(request, new PathValues());
        assertEquals("https", info.getBaseUri().getScheme());
    }

    @Test public void baseUriXForwardedHost() {
        Mockito.when(request.getHeader("X-Forwarded-Host")).thenReturn("google.com");
        UriInfo info = new UriInfoImpl(request, new PathValues());
        assertEquals("google.com", info.getBaseUri().getHost());
    }

    @Test public void baseUriWrongXForwardedHost() throws URISyntaxException {
        Mockito.when(request.getHeader("X-Forwarded-Host")).thenReturn("aaa .com");
        assertThrows(IllegalStateException.class, () -> new UriInfoImpl(request, new PathValues()));
    }

    @Test public void baseUriLocal() {
        Mockito.when(request.getHeader("Host")).thenReturn(null);
        Mockito.when(request.getLocalAddr()).thenReturn("2607:f0d0:1002:0051:0000:0000:0000:0004");
        Mockito.when(request.getLocalPort()).thenReturn(8080);
        UriInfo info = new UriInfoImpl(request, new PathValues());
        assertEquals("[2607:f0d0:1002:0051:0000:0000:0000:0004]:8080", info.getBaseUri().getAuthority());
    }

    @Test public void baseContextPath() {
        Mockito.when(request.getContextPath()).thenReturn("/");
        assertEquals("http://cactusthorn.net/", (new UriInfoImpl(request, new PathValues())).getBaseUri().toString());

        Mockito.when(request.getContextPath()).thenReturn("app/");
        assertEquals("http://cactusthorn.net/app/", new UriInfoImpl(request, new PathValues()).getBaseUri().toString());

        Mockito.when(request.getContextPath()).thenReturn("/app");
        assertEquals("http://cactusthorn.net/app/", new UriInfoImpl(request, new PathValues()).getBaseUri().toString());

        Mockito.when(request.getContextPath()).thenReturn("/app/");
        assertEquals("http://cactusthorn.net/app/", new UriInfoImpl(request, new PathValues()).getBaseUri().toString());
    }

    @Test public void getBaseUriBuilder() {
        assertEquals("http://cactusthorn.net/", new UriInfoImpl(request, new PathValues()).getBaseUriBuilder().build().toString());
    }

    @Test public void path() {
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        assertEquals("app/xxx", new UriInfoImpl(request, new PathValues()).getPath());

        Mockito.when(request.getPathInfo()).thenReturn("");
        assertEquals("", new UriInfoImpl(request, new PathValues()).getPath());

        Mockito.when(request.getPathInfo()).thenReturn(null);
        assertEquals("", new UriInfoImpl(request, new PathValues()).getPath());

        Mockito.when(request.getPathInfo()).thenReturn("/");
        assertEquals("", new UriInfoImpl(request, new PathValues()).getPath());
    }

    @Test public void pathEncoded() throws URISyntaxException {
        Mockito.when(request.getPathInfo()).thenReturn("app/xüxx");
        assertEquals("app/x%C3%BCxx", new UriInfoImpl(request, new PathValues()).getPath(false));
    }

    @Test public void getAbsolutePath() {
        Mockito.when(request.getContextPath()).thenReturn("cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt");
        Mockito.when(request.getPathInfo()).thenReturn("/app/xxx");
        assertEquals("http://cactusthorn.net/cntxt/srvlt/app/xxx", new UriInfoImpl(request, new PathValues()).getAbsolutePath().toString());
    }

    @Test public void getAbsolutePathBuilder() {
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        assertEquals("http://cactusthorn.net/app/xxx",
                new UriInfoImpl(request, new PathValues()).getAbsolutePathBuilder().build().toString());
    }

    @Test public void getRequestUri() {
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        Mockito.when(request.getQueryString()).thenReturn("aaa=bb cc");
        assertEquals("http://cactusthorn.net/app/xxx?aaa=bb%20cc", new UriInfoImpl(request, new PathValues()).getRequestUri().toString());
    }

    @Test public void getRequestNullQuery() {
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        assertEquals("http://cactusthorn.net/app/xxx", new UriInfoImpl(request, new PathValues()).getRequestUri().toString());
    }

    @Test public void getRequestUriBuilder() {
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        Mockito.when(request.getQueryString()).thenReturn("aaa=bb cc");
        assertEquals("http://cactusthorn.net/app/xxx?aaa=bb%20cc",
                new UriInfoImpl(request, new PathValues()).getRequestUriBuilder().build().toString());
    }

    @Test public void resolve() {
        Mockito.when(request.getContextPath()).thenReturn("/cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt/");
        Mockito.when(request.getPathInfo()).thenReturn("app/xxx");
        Mockito.when(request.getQueryString()).thenReturn("aaa=bb cc");
        assertEquals("http://cactusthorn.net/cntxt/srvlt/ssss/mmm?aa=bb",
                new UriInfoImpl(request, new PathValues()).resolve(URI.create("ssss/mmm?aa=bb")).toString());
    }

    @Test public void relativizeA() {
        Mockito.when(request.getContextPath()).thenReturn("/cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt/");
        Mockito.when(request.getPathInfo()).thenReturn("/a/b/c/resource.html");
        assertEquals("d/file.txt", new UriInfoImpl(request, new PathValues()).relativize(URI.create("a/b/c/d/file.txt")).toString());
    }

    @Test public void relativizeB() {
        Mockito.when(request.getContextPath()).thenReturn("/cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt/");
        Mockito.when(request.getPathInfo()).thenReturn("/a/b/c/resource.html");
        assertEquals("http://example2.com:9090/app2/root2/a/d/file.txt", new UriInfoImpl(request, new PathValues())
                .relativize(URI.create("http://example2.com:9090/app2/root2/a/d/file.txt")).toString());
    }

    @Test public void relativizeC() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        assertEquals("a/d/file.txt", new UriInfoImpl(request, new PathValues()).relativize(URI.create("/a/d/file.txt")).toString());
    }

    @Test public void relativizeD() {
        Mockito.when(request.getContextPath()).thenReturn("/cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt/");
        Mockito.when(request.getPathInfo()).thenReturn("/a/b/c/resource.html");
        assertEquals("file.txt", new UriInfoImpl(request, new PathValues()).relativize(URI.create("a/b/file.txt")).toString());
    }

    @Test public void relativizeE() throws URISyntaxException {
        Mockito.when(request.getPathInfo()).thenReturn("");
        URI to = new URI(null, null, null, "aa=bb", null);
        assertEquals("?aa=bb", new UriInfoImpl(request, new PathValues()).relativize(to).toString());
    }

    @Test public void relativizeOpaque() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        assertEquals("mailto:a@a.com", new UriInfoImpl(request, new PathValues()).relativize(URI.create("mailto:a@a.com")).toString());
    }

    @Test public void relativizeSchema() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        assertEquals("https://cactusthorn.net/a?a=b",
                new UriInfoImpl(request, new PathValues()).relativize(URI.create("https://cactusthorn.net/a?a=b")).toString());
    }

    @Test public void relativizeNoPath() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        assertEquals("",
                new UriInfoImpl(request, new PathValues()).relativize(URI.create("http://cactusthorn.net")).toString());
    }

    @Test public void getPathParameters() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        PathValues values = new PathValues();
        values.put("aa", "bb cc");
        values.put("bb", "xxxx");
        MultivaluedMap<String, String> map = new UriInfoImpl(request, values).getPathParameters();
        assertEquals("bb cc", map.getFirst("aa"));
        assertEquals("xxxx", map.getFirst("bb"));
        assertThrows(UnsupportedOperationException.class, () -> map.addFirst("xx", "yy"));
    }

    @Test public void getPathParametersEncode() {
        Mockito.when(request.getPathInfo()).thenReturn("");
        PathValues values = new PathValues();
        values.put("aa", "bb cc");
        values.put("bb", "xxxx");
        MultivaluedMap<String, String> map = new UriInfoImpl(request, values).getPathParameters(false);
        assertEquals("bb%20cc", map.getFirst("aa"));
        assertEquals("xxxx", map.getFirst("bb"));
    }

    @Test public void getQueryParameters() {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("aaa", new String[] { "aa bb", "ccdd" });

        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(parameters.keySet()));
        Mockito.when(request.getParameterValues("aaa")).thenReturn(parameters.get("aaa"));

        MultivaluedMap<String, String> map = new UriInfoImpl(request, new PathValues()).getQueryParameters();
        assertEquals("aa bb", map.get("aaa").get(0));
        assertEquals("ccdd", map.get("aaa").get(1));
        assertThrows(UnsupportedOperationException.class, () -> map.addFirst("xx", "yy"));
    }

    @Test public void getQueryParametersEncode() {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("aaa", new String[] { "aa bb", "ccdd" });

        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(parameters.keySet()));
        Mockito.when(request.getParameterValues("aaa")).thenReturn(parameters.get("aaa"));

        MultivaluedMap<String, String> map = new UriInfoImpl(request, new PathValues()).getQueryParameters(false);
        assertEquals("aa%20bb", map.get("aaa").get(0));
    }
}
