package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

public class HttpTest {

    @Test //
    public void sort() {

        List<MediaType> list = new ArrayList<>();
        list.add(new MediaType("test", "html", addQ("0.5")));
        list.add(MediaType.WILDCARD_TYPE);
        list.add(null);
        list.add(MediaType.TEXT_PLAIN_TYPE);
        list.add(new MediaType("application", "json", addQ("0.875")));
        list.add(new MediaType("test", "*"));
        list.add(new MediaType("application", "*"));
        list.add(new MediaType("*", "json"));
        list.add(null);

        Collections.sort(list, Http.ACCEPT_COMPARATOR);

        assertEquals("text/plain", _toString(list.get(0)));
        assertEquals("application/json", _toString(list.get(1)));
        assertEquals("test/html", _toString(list.get(2)));
        assertEquals("test/*", _toString(list.get(3)));
        assertEquals("application/*", _toString(list.get(4)));
        assertEquals("*/json", _toString(list.get(5)));
        assertEquals("*/*", _toString(list.get(6)));
        assertEquals("NULL", _toString(list.get(7)));
        assertEquals("NULL", _toString(list.get(8)));
    }

    private Map<String, String> addQ(String value) {
        Map<String, String> result = new HashMap<>();
        result.put("q", value);
        return result;
    }

    private String _toString(MediaType mediaType) {
        if (mediaType == null) {
            return "NULL";
        }
        return mediaType.getType() + '/' + mediaType.getSubtype();
    }

    @Test //
    public void parseAccept() {
        List<String> accept = new ArrayList<>();
        accept.add(
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");

        List<MediaType> mediaTypes = Http.parseAccept(Collections.enumeration(accept));
        assertEquals("text/html", mediaTypes.get(0).toString());
        assertEquals("application/xhtml+xml", mediaTypes.get(1).toString());
        assertEquals("image/avif", mediaTypes.get(2).toString());
        assertEquals("image/webp", mediaTypes.get(3).toString());
        assertEquals("image/apng", mediaTypes.get(4).toString());
        assertEquals("application/xml;q=0.9", mediaTypes.get(5).toString());
        assertEquals("application/signed-exchange;q=0.9;v=b3", mediaTypes.get(6).toString());
        assertEquals("*/*;q=0.8", mediaTypes.get(7).toString());
    }

    @Test //
    public void parseEmptyAccept() {
        List<MediaType> mediaTypes = Http.parseAccept(Collections.emptyEnumeration());
        assertEquals(MediaType.WILDCARD, mediaTypes.get(0).toString());
    }

    @Test //
    public void parseCookieSimple() {
        String cookieHeader = "aa=bb; cc=dd";
        List<Cookie> cookies = Http.parseCookies(cookieHeader);
        assertEquals(2, cookies.size());
        assertEquals("$Version=0;aa=bb", cookies.get(0).toString());
        assertEquals("$Version=0;cc=dd", cookies.get(1).toString());
    }

    @Test //
    public void parseCookieVersion() {
        String cookieHeader = "$Version=\"1\"; Customer=\"WILE_E_COYOTE\"; $Path=\"/acme\"";
        List<Cookie> cookies = Http.parseCookies(cookieHeader);
        assertEquals("$Version=1;Customer=WILE_E_COYOTE;$Path=/acme", cookies.get(0).toString());
    }

    @Test //
    public void parseMultipleCompelxCookie() {
        String cookieHeader = "$Version=\"1\"; aa=\"WILE E COYOTE\"; $Path=\"/acme\", cc=dd, tt=mm, $Domain=\"DO MAIN\"";
        List<Cookie> cookies = Http.parseCookies(cookieHeader);
        assertEquals("$Version=1;aa=\"WILE E COYOTE\";$Path=/acme", cookies.get(0).toString());
        assertEquals("$Version=1;cc=dd", cookies.get(1).toString());
        assertEquals("$Version=1;tt=mm;$Domain=\"DO MAIN\"", cookies.get(2).toString());
    }
}
