package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Variant;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.ResponseImpl.ResponseBuilderImpl;

public class ResponseImplTest {

    @Test public void _clone() {
        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.status(200, "OK");
        ResponseBuilder clonned = builder.clone();
        assertNotEquals(clonned.hashCode(), builder.hashCode());
    }

    @Test public void allow() {
        Response response = new ResponseBuilderImpl().allow("GET", "post", "PUT,PATCH").allow("POst, ").build();
        Set<String> methods = response.getAllowedMethods();
        assertEquals(4, response.getAllowedMethods().size());
        assertTrue(methods.contains("GET"));
        assertTrue(methods.contains("POST"));
        assertTrue(methods.contains("PUT"));
        assertTrue(methods.contains("PATCH"));

        response = new ResponseBuilderImpl().header(HttpHeaders.ALLOW, new Date()).build();
        assertTrue(response.getAllowedMethods().isEmpty());

        response = new ResponseBuilderImpl().header(HttpHeaders.ALLOW, new Date()).build();
        assertTrue(response.getAllowedMethods().isEmpty());

        response = new ResponseBuilderImpl().allow("GET", "POST", "PUT,PATCH").allow((String[]) null).build();
        assertTrue(response.getAllowedMethods().isEmpty());

        response = new ResponseBuilderImpl().allow(new String[0]).build();
        assertTrue(response.getAllowedMethods().isEmpty());

        response = new ResponseBuilderImpl().allow("GET", "POST", "PUT,PATCH").allow((Set<String>) null).build();
        assertTrue(response.getAllowedMethods().isEmpty());
    }

    @Test public void status() {
        Response response = new ResponseBuilderImpl().status(588, "test it").build();
        StatusType statusType = response.getStatusInfo();
        assertEquals(588, statusType.getStatusCode());
        assertEquals("test it", statusType.getReasonPhrase());
    }

    @Test public void statusSimple() {
        Response response = new ResponseBuilderImpl().status(204).build();
        StatusType statusType = response.getStatusInfo();
        assertEquals(204, statusType.getStatusCode());
        assertEquals(204, response.getStatus());
    }

    @Test @SuppressWarnings("unchecked") public void responseUnsupported() {
        Response response = Response.ok().build();
        assertThrows(UnsupportedOperationException.class, () -> response.readEntity(Date.class));
        assertThrows(UnsupportedOperationException.class, () -> response.readEntity(Date.class, null));
        assertThrows(UnsupportedOperationException.class, () -> response.readEntity(GenericType.forInstance(Date.class)));
        assertThrows(UnsupportedOperationException.class, () -> response.readEntity(GenericType.forInstance(Date.class), null));
        assertThrows(UnsupportedOperationException.class, () -> response.close());
        assertFalse(response.bufferEntity());
    }

    @Test public void responseBuilderUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> Response.ok().entity(null, null));
    }

    @Test public void statusUnknown() {
        Response response = new ResponseBuilderImpl().status(588).build();
        StatusType statusType = response.getStatusInfo();
        assertNotNull(statusType.getFamily());
        assertEquals(588, statusType.getStatusCode());
        assertEquals("Unknown", statusType.getReasonPhrase());
    }

    @Test public void wrongStatus() {
        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.status(100);
        builder.status(599);
        assertThrows(IllegalArgumentException.class, () -> builder.status(50));
        assertThrows(IllegalArgumentException.class, () -> builder.status(99));
        assertThrows(IllegalArgumentException.class, () -> builder.status(600));
        assertThrows(IllegalArgumentException.class, () -> builder.status(700));
    }

    @Test public void entity() {
        Response response = new ResponseBuilderImpl().entity("ABC").build();
        assertTrue(response.hasEntity());
        assertEquals("ABC", response.getEntity());
    }

    @Test public void cacheControl() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setPrivate(true);
        cacheControl.setNoTransform(false);
        cacheControl.setMaxAge(111);

        Response response = new ResponseBuilderImpl().cacheControl(cacheControl).build();
        assertEquals("private, max-age=111", response.getHeaderString(HttpHeaders.CACHE_CONTROL));

        response = new ResponseBuilderImpl().cacheControl(cacheControl).cacheControl(null).build();
        assertNull(response.getHeaderString(HttpHeaders.CACHE_CONTROL));
    }

    @Test public void contentEncoding() {
        Response response = new ResponseBuilderImpl().encoding("gzip, deflate").build();
        assertEquals("gzip, deflate", response.getHeaderString(HttpHeaders.CONTENT_ENCODING));

        response = new ResponseBuilderImpl().encoding("gzip, deflate").encoding(null).build();
        assertNull(response.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test public void header() {
        Response response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, 10).header(HttpHeaders.CONTENT_LENGTH, 20).build();
        assertEquals("20", response.getHeaderString(HttpHeaders.CONTENT_LENGTH));

        response = new ResponseBuilderImpl().header("A", 10).header("A", 20).build();
        assertEquals("10,20", response.getHeaderString("A"));

        response = new ResponseBuilderImpl().header("A", 10).header("A", null).build();
        assertNull(response.getHeaderString("A"));
    }

    @Test public void replaceAll() {
        MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
        map.add(HttpHeaders.CONTENT_LENGTH, 30);
        Response response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, 10).replaceAll(map).build();
        assertEquals("30", response.getHeaderString(HttpHeaders.CONTENT_LENGTH));

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, 10).replaceAll(null).build();
        assertNull(response.getHeaderString(HttpHeaders.CONTENT_LENGTH));
    }

    @Test public void language() {
        Response response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LANGUAGE, "en-GB").build();
        assertEquals("en-GB", response.getLanguage().toLanguageTag());

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LANGUAGE, new Date()).build();
        assertNull(response.getLanguage());

        response = new ResponseBuilderImpl().language("en-GB").build();
        assertEquals("en-GB", response.getLanguage().toLanguageTag());

        response = new ResponseBuilderImpl().language("en-GB").language((String) null).build();
        assertNull(response.getLanguage());

        response = new ResponseBuilderImpl().language(new Locale("en", "GB")).build();
        assertEquals("en-GB", response.getLanguage().toLanguageTag());

        response = new ResponseBuilderImpl().language(new Locale("en", "GB")).language((Locale) null).build();
        assertNull(response.getLanguage());
    }

    @Test public void mediaType() {
        Response response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML).build();
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_TYPE, new Date()).build();
        assertNull(response.getMediaType());

        response = new ResponseBuilderImpl().type(MediaType.TEXT_HTML).build();
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());

        response = new ResponseBuilderImpl().type(MediaType.TEXT_HTML).type((String) null).build();
        assertNull(response.getMediaType());

        response = new ResponseBuilderImpl().type(MediaType.TEXT_HTML_TYPE).build();
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());

        response = new ResponseBuilderImpl().type(MediaType.TEXT_HTML_TYPE).type((MediaType) null).build();
        assertNull(response.getMediaType());
    }

    @Test public void contentLocation() throws URISyntaxException {
        Response response = new ResponseBuilderImpl().contentLocation(new URI("aaa/bbb")).build();
        assertEquals("aaa/bbb", response.getHeaderString(HttpHeaders.CONTENT_LOCATION));

        response = new ResponseBuilderImpl().contentLocation(new URI("aaa/bbb")).contentLocation(null).build();
        assertNull(response.getHeaderString(HttpHeaders.CONTENT_LOCATION));
    }

    @Test public void cookie() {
        NewCookie cookie = new NewCookie("aaaa", "bbbb");
        Response response = new ResponseBuilderImpl().cookie(cookie).cookie(new NewCookie[0]).build();
        Map<String, NewCookie> map = response.getCookies();
        assertEquals("aaaa=bbbb;Version=1", map.get("aaaa").toString());

        response = new ResponseBuilderImpl().cookie(cookie).cookie((NewCookie[]) null).build();
        assertNull(response.getHeaderString(HttpHeaders.SET_COOKIE));

        response = Response.ok().build();
        assertTrue(response.getCookies().isEmpty());

        response = Response.ok().header(HttpHeaders.SET_COOKIE, "aaaa=bbbb;Version=3").build();
        assertEquals("aaaa=bbbb;Version=3", response.getCookies().get("aaaa").toString());

        response = Response.ok().header(HttpHeaders.SET_COOKIE, new Date()).build();
        assertTrue(response.getCookies().isEmpty());
    }

    @Test public void expires() {
        Date date = new Date();
        Response response = new ResponseBuilderImpl().expires(date).build();
        assertNotNull(response.getHeaderString(HttpHeaders.EXPIRES));

        response = new ResponseBuilderImpl().expires(date).expires(null).build();
        assertNull(response.getHeaderString(HttpHeaders.EXPIRES));
    }

    @Test public void lastModified() {
        Date date = new Date();
        Response response = Response.ok().lastModified(date).build();
        assertNotNull(response.getLastModified());

        response = Response.ok().header(HttpHeaders.LAST_MODIFIED, "Thu, 01 Dec 1994 16:00:00 GMT").build();
        assertNotNull(response.getLastModified());

        response = Response.ok().header(HttpHeaders.LAST_MODIFIED, 10).build();
        assertNull(response.getLastModified());

        response = Response.ok().build();
        assertNull(response.getLastModified());

        response = Response.ok().lastModified(date).lastModified(null).build();
        assertNull(response.getHeaderString(HttpHeaders.LAST_MODIFIED));
    }

    @Test public void date() {
        Date date = new Date();
        Response response = Response.ok().header(HttpHeaders.DATE, date).build();
        assertNotNull(response.getDate());
    }

    @Test public void location() throws URISyntaxException {
        Response response = Response.ok().location(new URI("aaa/bbb")).build();
        assertEquals("aaa/bbb", response.getLocation().toASCIIString());

        response = Response.ok().header(HttpHeaders.LOCATION, "aaa/bbb").build();
        assertEquals("aaa/bbb", response.getLocation().toASCIIString());

        response = new ResponseBuilderImpl().location(new URI("aaa/bbb")).location(null).build();
        assertNull(response.getLocation());

        response = Response.ok().header(HttpHeaders.LOCATION, new Date()).build();
        assertNull(response.getLocation());
    }

    @Test public void tag() {
        Response response = new ResponseBuilderImpl().header(HttpHeaders.ETAG, "\"abc\"").build();
        assertEquals("\"abc\"", response.getEntityTag().toString());

        response = new ResponseBuilderImpl().header(HttpHeaders.ETAG, new Date()).build();
        assertNull(response.getEntityTag());

        response = new ResponseBuilderImpl().tag("\"abc\"").build();
        assertEquals("\"abc\"", response.getEntityTag().toString());

        response = new ResponseBuilderImpl().tag("\"abc\"").tag((String) null).build();
        assertNull(response.getEntityTag());

        EntityTag t = new EntityTag("abc");
        response = new ResponseBuilderImpl().tag(t).build();
        assertEquals("\"abc\"", response.getEntityTag().toString());

        response = new ResponseBuilderImpl().tag(t).tag((EntityTag) null).build();
        assertNull(response.getEntityTag());
    }

    @Test public void length() {
        Response response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, 10).build();
        assertEquals(10, response.getLength());

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, new Date()).build();
        assertEquals(-1, response.getLength());

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, null).build();
        assertEquals(-1, response.getLength());

        response = new ResponseBuilderImpl().header(HttpHeaders.CONTENT_LENGTH, "20").build();
        assertEquals(20, response.getLength());
    }

    @Test public void getStringHeaders() {
        Response response = Response.ok().header(HttpHeaders.LOCATION, "aaa/bbb").build();
        response.getStringHeaders();
    }

    @Test public void variant() {
        Variant variant = new Variant(MediaType.TEXT_HTML_TYPE, "en", "GB", "UTF-8");
        Response response = Response.ok().variant(variant).build();
        assertEquals(MediaType.TEXT_HTML_TYPE, response.getMediaType());
        assertEquals(new Locale("en", "GB"), response.getLanguage());
        assertEquals("UTF-8", response.getHeaderString(HttpHeaders.CONTENT_ENCODING));

        response = Response.ok().variant(variant).variant(null).build();
        assertNull(response.getMediaType());
        assertNull(response.getLanguage());
        assertNull(response.getHeaderString(HttpHeaders.CONTENT_ENCODING));
    }

    @Test public void variants() {
        Variant variant = new Variant(MediaType.TEXT_HTML_TYPE, "en", "GB", "UTF-8");
        Response response = Response.ok().variants(variant).build();
        assertEquals("Accept,Accept-Language,Accept-Encoding", response.getHeaderString(HttpHeaders.VARY));

        response = Response.ok().variants(variant).variants((Variant[]) null).build();
        assertNull(response.getHeaderString(HttpHeaders.VARY));

        response = Response.ok().variants(variant).variants((List<Variant>) null).build();
        assertNull(response.getHeaderString(HttpHeaders.VARY));

        response = Response.ok().variants(variant).variants(Collections.emptyList()).build();
        assertEquals("Accept,Accept-Language,Accept-Encoding", response.getHeaderString(HttpHeaders.VARY));

        variant = new Variant(MediaType.TEXT_HTML_TYPE, (String) null, null);
        response = Response.ok().variants(variant).build();
        assertEquals("Accept", response.getHeaderString(HttpHeaders.VARY));

        variant = new Variant(null, "en", "GB", null);
        response = Response.ok().variants(variant).build();
        assertEquals("Accept-Language", response.getHeaderString(HttpHeaders.VARY));

        variant = new Variant(null, (String) null, "UTF-8");
        response = Response.ok().variants(variant).build();
        assertEquals("Accept-Encoding", response.getHeaderString(HttpHeaders.VARY));

        variant = new Variant(MediaType.TEXT_HTML_TYPE, (String) null, null);
        Variant variant2 = new Variant(null, (String) null, "UTF-8");
        Variant variant3 = new Variant(null, "en", "GB", null);
        response = Response.ok().variants(variant, variant2, variant3).build();
        assertEquals("Accept,Accept-Language,Accept-Encoding", response.getHeaderString(HttpHeaders.VARY));
    }

    @Test public void links() {
        Link link = Link.valueOf("<example.com>; rel=abc");
        Link link2 = Link.valueOf("<test.net>");
        Response response = Response.ok().links(link, link2).build();
        assertEquals(2, response.getLinks().size());
        assertTrue(response.hasLink("abc"));
        assertEquals(link.toString(), response.getLink("abc").toString());
        assertNotNull(response.getLinkBuilder("abc"));

        response = Response.ok().links(link, link2).links((Link[]) null).build();
        assertEquals(0, response.getLinks().size());

        response = Response.ok().link("example.com", "zxy").build();
        assertFalse(response.hasLink("abc"));
        assertNull(response.getLink("abc"));
        assertNull(response.getLinkBuilder("abc"));

        response = Response.ok().links(link, link2).links(new Link[0]).build();
        assertEquals(2, response.getLinks().size());

        response = Response.ok().links(link).header(HttpHeaders.LINK, "<test.net>").build();
        assertEquals(2, response.getLinks().size());

        response = Response.ok().header(HttpHeaders.LINK, new Date()).build();
        assertEquals(0, response.getLinks().size());
    }
}
