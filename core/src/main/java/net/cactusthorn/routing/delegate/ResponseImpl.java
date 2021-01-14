package net.cactusthorn.routing.delegate;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class ResponseImpl extends Response {

    private int status;
    private String reasonPhrase;
    private Object entity;
    private MultivaluedMap<String, Object> headers;

    public ResponseImpl(int status, String reasonPhrase, Object entity, MultivaluedMap<String, Object> headers) {
        this.status = status;
        this.reasonPhrase = reasonPhrase;
        this.entity = entity;
        this.headers = headers;
    }

    @Override public int getStatus() {
        return status;
    }

    @Override public StatusType getStatusInfo() {
        if (reasonPhrase == null) {
            Status statusInstance = Status.fromStatusCode(status);
            if (statusInstance != null) {
                return statusInstance;
            }
        }
        return new StatusType() {
            @Override public int getStatusCode() {
                return status;
            }

            @Override public Status.Family getFamily() {
                return Status.Family.familyOf(status);
            }

            @Override public String getReasonPhrase() {
                return reasonPhrase == null ? "Unknown" : reasonPhrase;
            }
        };
    }

    @Override public Object getEntity() {
        return entity;
    }

    @Override public <T> T readEntity(Class<T> entityType) {
        throw new UnsupportedOperationException();
    }

    @Override public <T> T readEntity(GenericType<T> entityType) {
        throw new UnsupportedOperationException();
    }

    @Override public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @Override public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean hasEntity() {
        return Objects.nonNull(entity);
    }

    @Override public boolean bufferEntity() {
        return false;
    }

    @Override public void close() {
        throw new UnsupportedOperationException();
    }

    @Override public MediaType getMediaType() {
        Object header = getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (header == null) {
            return null;
        }
        if (header instanceof MediaType) {
            return (MediaType) header;
        }
        if (header instanceof String) {
            return MediaType.valueOf((String) header);
        }
        return null;
    }

    @Override public Locale getLanguage() {
        Object header = getHeaders().getFirst(HttpHeaders.CONTENT_LANGUAGE);
        if (header == null) {
            return null;
        }
        if (header instanceof Locale) {
            return (Locale) header;
        }
        if (header instanceof String) {
            return RuntimeDelegate.getInstance().createHeaderDelegate(Locale.class).fromString((String) header);
        }
        return null;
    }

    @Override public int getLength() {
        Object value = getHeaders().getFirst(HttpHeaders.CONTENT_LENGTH);
        if (value == null) {
            return -1;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            return Integer.valueOf((String) value);
        }
        return -1;
    }

    @Override public Set<String> getAllowedMethods() {
        List<Object> allows = getHeaders().get(HttpHeaders.ALLOW);
        if (allows == null) {
            return Collections.emptySet();
        }
        Set<String> methods = new LinkedHashSet<>();
        for (Object allow : allows) {
            if (allow instanceof String) {
                for (String part : ((String) allow).split(",")) {
                    if (!part.trim().isEmpty()) {
                        methods.add(part.trim());
                    }
                }
            }
        }
        return methods;
    }

    @Override public Map<String, NewCookie> getCookies() {
        List<Object> setCookie = getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookie == null) {
            return Collections.emptyMap();
        }
        Map<String, NewCookie> cookies = new HashMap<>();
        for (Object cookie : setCookie) {
            if (cookie instanceof NewCookie) {
                NewCookie newCookie = (NewCookie) cookie;
                cookies.put(newCookie.getName(), newCookie);
            } else if (cookie instanceof String) {
                NewCookie newCookie = NewCookie.valueOf((String) cookie);
                cookies.put(newCookie.getName(), newCookie);
            }
        }
        return cookies;
    }

    @Override public EntityTag getEntityTag() {
        Object header = getHeaders().getFirst(HttpHeaders.ETAG);
        if (header == null) {
            return null;
        }
        if (header instanceof EntityTag) {
            return (EntityTag) header;
        }
        if (header instanceof String) {
            return EntityTag.valueOf((String) header);
        }
        return null;
    }

    @Override public Date getDate() {
        return getDateHeader(HttpHeaders.DATE);
    }

    @Override public Date getLastModified() {
        return getDateHeader(HttpHeaders.LAST_MODIFIED);
    }

    private Date getDateHeader(String name) {
        Object header = getHeaders().getFirst(name);
        if (header == null) {
            return null;
        }
        if (header instanceof Date) {
            return (Date) header;
        }
        if (header instanceof String) {
            return RuntimeDelegate.getInstance().createHeaderDelegate(Date.class).fromString((String) header);
        }
        return null;
    }

    @Override public URI getLocation() {
        Object header = getHeaders().getFirst(HttpHeaders.LOCATION);
        if (header == null) {
            return null;
        }
        if (header instanceof URI) {
            return (URI) header;
        }
        if (header instanceof String) {
            return RuntimeDelegate.getInstance().createHeaderDelegate(URI.class).fromString((String) header);
        }
        return null;
    }

    @Override public Set<Link> getLinks() {
        List<Object> links = getHeaders().get(HttpHeaders.LINK);
        if (links == null) {
            return Collections.emptySet();
        }
        Set<Link> linkSet = new LinkedHashSet<>();
        for (Object value : links) {
            if (value instanceof Link) {
                linkSet.add((Link) value);
            } else if (value instanceof String) {
                linkSet.add(Link.valueOf((String) value));
            }
        }
        return linkSet;
    }

    @Override public boolean hasLink(String relation) {
        for (Link link : getLinks()) {
            if (link.getRels().contains(relation)) {
                return true;
            }
        }
        return false;
    }

    @Override public Link getLink(String relation) {
        for (Link link : getLinks()) {
            if (link.getRels().contains(relation)) {
                return link;
            }
        }
        return null;
    }

    @Override public Builder getLinkBuilder(String relation) {
        Link link = getLink(relation);
        if (link == null) {
            return null;
        }
        return Link.fromLink(link);
    }

    @Override public MultivaluedMap<String, Object> getMetadata() {
        return headers;
    }

    @Override public MultivaluedMap<String, String> getStringHeaders() {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        for (Map.Entry<String, List<Object>> entry : getHeaders().entrySet()) {
            for (Object value : entry.getValue()) {
                result.add(entry.getKey(), valueAsString(value));
            }
        }
        return result;
    }

    @Override public String getHeaderString(String name) {
        List<Object> header = getHeaders().get(name);
        if (header == null) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(",");
        for (Object value : header) {
            joiner.add(valueAsString(value));
        }
        return joiner.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) private String valueAsString(Object value) {
        HeaderDelegate headerDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(value.getClass());
        if (headerDelegate != null) {
            return headerDelegate.toString(value);
        } else {
            return value.toString();
        }
    }

    public static class ResponseBuilderImpl extends ResponseBuilder implements Cloneable {

        private int status = Response.Status.OK.getStatusCode();
        private String statusReasonPhrase;
        private Object entity;
        private final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        public ResponseBuilderImpl() {
        }

        private ResponseBuilderImpl(ResponseBuilderImpl builder) {
            this.status = builder.status;
            this.statusReasonPhrase = builder.statusReasonPhrase;
            this.entity = builder.entity;
            this.headers.putAll(builder.headers);
        }

        private void reset() {
            status = Response.Status.OK.getStatusCode();
            statusReasonPhrase = null;
            entity = null;
            headers.clear();
        }

        @Override public Response build() {
            MultivaluedMap<String, Object> clone = new MultivaluedHashMap<>();
            clone.putAll(headers);
            Response response = new ResponseImpl(status, statusReasonPhrase, entity, clone);
            reset();
            return response;
        }

        @Override public ResponseBuilder clone() {
            return new ResponseBuilderImpl(this);
        }

        private static final int MIN_STATUS = 100;
        private static final int MAX_STATUS = 599;

        @Override public ResponseBuilder status(int sstatus) {
            if (sstatus < MIN_STATUS || sstatus > MAX_STATUS) {
                throw new IllegalArgumentException("status code must be >= 100 and <= 599");
            }
            this.status = sstatus;
            return this;
        }

        @Override public ResponseBuilder status(int sstatus, String sstatusReasonPhrase) {
            status(sstatus);
            this.statusReasonPhrase = sstatusReasonPhrase;
            return this;
        }

        @Override public ResponseBuilder entity(Object eentity) {
            this.entity = eentity;
            return this;
        }

        @Override public ResponseBuilder entity(Object eentity, Annotation[] aannotations) {
            throw new UnsupportedOperationException();
        }

        @Override public ResponseBuilder allow(String... methods) {
            if (methods == null) {
                headers.remove(HttpHeaders.ALLOW);
                return this;
            }
            if (methods.length == 0) {
                return this;
            }
            Set<String> set = new HashSet<>(Arrays.asList(methods));
            return allow(set);
        }

        @Override public ResponseBuilder allow(Set<String> methods) {
            if (methods == null) {
                headers.remove(HttpHeaders.ALLOW);
            } else {
                methods.forEach(m -> headers.add(HttpHeaders.ALLOW, m.toUpperCase()));
            }
            return this;
        }

        @Override public ResponseBuilder cacheControl(CacheControl cacheControl) {
            if (cacheControl == null) {
                headers.remove(HttpHeaders.CACHE_CONTROL);
            } else {
                headers.putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
            }
            return this;
        }

        @Override public ResponseBuilder encoding(String encoding) {
            if (encoding == null) {
                headers.remove(HttpHeaders.CONTENT_ENCODING);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
            }
            return this;
        }

        private static final Set<String> SINGLE_VALUE_HEADERS = new HashSet<>(Arrays.asList(HttpHeaders.CACHE_CONTROL,
                HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.CONTENT_LOCATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.ETAG, HttpHeaders.LAST_MODIFIED, HttpHeaders.LOCATION, HttpHeaders.EXPIRES));

        @Override public ResponseBuilder header(String name, Object value) {
            if (value == null) {
                headers.remove(name);
            } else if (SINGLE_VALUE_HEADERS.contains(name)) {
                headers.putSingle(name, value);
            } else {
                headers.add(name, value);
            }
            return this;
        }

        @Override public ResponseBuilder replaceAll(MultivaluedMap<String, Object> headerss) {
            this.headers.clear();
            if (headerss != null) {
                this.headers.putAll(headerss);
            }
            return this;
        }

        @Override public ResponseBuilder language(String language) {
            if (language == null) {
                headers.remove(HttpHeaders.CONTENT_LANGUAGE);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_LANGUAGE, language);
            }
            return this;
        }

        @Override public ResponseBuilder language(Locale language) {
            if (language == null) {
                headers.remove(HttpHeaders.CONTENT_LANGUAGE);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_LANGUAGE, language);
            }
            return this;
        }

        @Override public ResponseBuilder type(MediaType type) {
            if (type == null) {
                headers.remove(HttpHeaders.CONTENT_TYPE);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_TYPE, type);
            }
            return this;
        }

        @Override public ResponseBuilder type(String type) {
            if (type == null) {
                headers.remove(HttpHeaders.CONTENT_TYPE);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_TYPE, type);
            }
            return this;
        }

        @Override public ResponseBuilder variant(Variant variant) {
            if (variant == null) {
                type((String) null);
                language((String) null);
                encoding(null);
            } else {
                type(variant.getMediaType());
                language(variant.getLanguage());
                encoding(variant.getEncoding());
            }
            return this;
        }

        @Override public ResponseBuilder contentLocation(URI location) {
            if (location == null) {
                headers.remove(HttpHeaders.CONTENT_LOCATION);
            } else {
                headers.putSingle(HttpHeaders.CONTENT_LOCATION, location);
            }
            return this;
        }

        @Override public ResponseBuilder cookie(NewCookie... cookies) {
            if (cookies == null) {
                headers.remove(HttpHeaders.SET_COOKIE);
                return this;
            }
            if (cookies.length == 0) {
                return this;
            }
            for (NewCookie cookie : cookies) {
                headers.add(HttpHeaders.SET_COOKIE, cookie);
            }
            return this;
        }

        @Override public ResponseBuilder expires(Date expires) {
            if (expires == null) {
                headers.remove(HttpHeaders.EXPIRES);
            } else {
                headers.putSingle(HttpHeaders.EXPIRES, expires);
            }
            return this;
        }

        @Override public ResponseBuilder lastModified(Date lastModified) {
            if (lastModified == null) {
                headers.remove(HttpHeaders.LAST_MODIFIED);
            } else {
                headers.putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
            }
            return this;
        }

        @Override public ResponseBuilder location(URI location) {
            if (location == null) {
                headers.remove(HttpHeaders.LOCATION);
            } else {
                headers.putSingle(HttpHeaders.LOCATION, location);
            }
            return this;
        }

        @Override public ResponseBuilder tag(EntityTag tag) {
            if (tag == null) {
                headers.remove(HttpHeaders.ETAG);
            } else {
                headers.putSingle(HttpHeaders.ETAG, tag);
            }
            return this;
        }

        @Override public ResponseBuilder tag(String tag) {
            if (tag == null) {
                headers.remove(HttpHeaders.ETAG);
            } else {
                headers.putSingle(HttpHeaders.ETAG, tag);
            }
            return this;
        }

        @Override public ResponseBuilder variants(Variant... variants) {
            return variants(variants == null ? null : Arrays.asList(variants));
        }

        @Override public ResponseBuilder variants(List<Variant> variants) {
            if (variants == null) {
                headers.remove(HttpHeaders.VARY);
                return this;
            }
            if (variants.isEmpty()) {
                return this;
            }

            boolean acceptMediaType = false;
            boolean acceptLanguage = false;
            boolean acceptEncoding = false;

            for (Variant variant : variants) {
                acceptMediaType |= variant.getMediaType() != null;
                acceptLanguage |= variant.getLanguage() != null;
                acceptEncoding |= variant.getEncoding() != null;
            }

            StringJoiner joiner = new StringJoiner(",");
            if (acceptMediaType) {
                joiner.add(HttpHeaders.ACCEPT);
            }
            if (acceptLanguage) {
                joiner.add(HttpHeaders.ACCEPT_LANGUAGE);
            }
            if (acceptEncoding) {
                joiner.add(HttpHeaders.ACCEPT_ENCODING);
            }

            header(HttpHeaders.VARY, joiner.toString());

            return this;
        }

        @Override public ResponseBuilder links(Link... links) {
            if (links == null) {
                headers.remove(HttpHeaders.LINK);
                return this;
            }
            if (links.length == 0) {
                return this;
            }
            for (Link link : links) {
                headers.add(HttpHeaders.LINK, link);
            }
            return this;
        }

        @Override public ResponseBuilder link(URI uri, String rel) {
            headers.add(HttpHeaders.LINK, Link.fromUri(uri).rel(rel).build());
            return this;
        }

        @Override public ResponseBuilder link(String uri, String rel) {
            return link(URI.create(uri), rel);
        }
    }
}
