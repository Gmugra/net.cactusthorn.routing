package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.util.CaseInsensitiveMultivaluedMap;
import net.cactusthorn.routing.util.Headers;
import net.cactusthorn.routing.util.UnmodifiableMultivaluedMap;

public class HttpHeadersParameter extends MethodParameter {

    public HttpHeadersParameter(Method method, Parameter parameter, Type genericType, int position) {
        super(method, parameter, genericType, position);
    }

    @Override //
    public HttpHeaders findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return new HttpHeadersImpl(req);
    }

    static final class HttpHeadersImpl implements HttpHeaders {

        private static final MultivaluedMap<String, String> EMPTY = new UnmodifiableMultivaluedMap<>(new MultivaluedHashMap<>());

        private MultivaluedMap<String, String> map;

        private Locale locale;
        private MediaType mediaType;
        private Date date;
        private List<MediaType> accept;
        private Map<String, Cookie> cookies;
        private List<Locale> acceptLanguage;

        HttpHeadersImpl(HttpServletRequest req) {
            Enumeration<String> headerNames = req.getHeaderNames();
            if (headerNames == null || !headerNames.hasMoreElements()) {
                map = EMPTY;
            } else {
                MultivaluedMap<String, String> headers = new CaseInsensitiveMultivaluedMap<String>();
                for (Enumeration<String> names = headerNames; names.hasMoreElements();) {
                    String name = names.nextElement();
                    headers.addAll(name, Collections.list(req.getHeaders(name)));
                }
                map = new UnmodifiableMultivaluedMap<>(headers);
            }
            locale = parseLocale();
            mediaType = parseMediaType();
            date = parseDate();
            accept = Headers.parseAccept(map.getFirst(ACCEPT));
            cookies = parseCookies();
            acceptLanguage = Headers.parseAcceptLanguage(map.getFirst(ACCEPT_LANGUAGE));
        }

        @Override public List<String> getRequestHeader(String name) {
            return map.get(name);
        }

        @Override public String getHeaderString(String name) {
            List<String> values = map.get(name);
            if (values == null) {
                return null;
            }
            StringJoiner joiner = new StringJoiner(",");
            for (String value : values) {
                joiner.add(value);
            }
            return joiner.toString();
        }

        @Override public MultivaluedMap<String, String> getRequestHeaders() {
            return map;
        }

        @Override public List<MediaType> getAcceptableMediaTypes() {
            return accept;
        }

        @Override public List<Locale> getAcceptableLanguages() {
            return acceptLanguage;
        }

        @Override public MediaType getMediaType() {
            return mediaType;
        }

        @Override public Locale getLanguage() {
            return locale;
        }

        @Override public Map<String, Cookie> getCookies() {
            return cookies;
        }

        @Override public Date getDate() {
            return date;
        }

        @Override public int getLength() {
            String header = map.getFirst(CONTENT_LENGTH);
            if (header == null) {
                return -1;
            }
            return Integer.parseInt(header);
        }

        private Locale parseLocale() {
            String header = map.getFirst(CONTENT_LANGUAGE);
            if (header == null) {
                return null;
            }
            return RuntimeDelegate.getInstance().createHeaderDelegate(Locale.class).fromString(header);
        }

        private MediaType parseMediaType() {
            String header = map.getFirst(CONTENT_TYPE);
            if (header == null) {
                return null;
            }
            return MediaType.valueOf(header);
        }

        private Date parseDate() {
            String header = map.getFirst(DATE);
            if (header == null) {
                return null;
            }
            return RuntimeDelegate.getInstance().createHeaderDelegate(Date.class).fromString(header);
        }

        private Map<String, Cookie> parseCookies() {
            String header = map.getFirst(COOKIE);
            if (header == null) {
                return Collections.emptyMap();
            }
            Map<String, Cookie> result = new HashMap<>();
            List<Cookie> list = Headers.parseCookies(header);
            for (Cookie cookie : list) {
                result.putIfAbsent(cookie.getName(), cookie);
            }
            return Collections.unmodifiableMap(result);
        }
    }
}
