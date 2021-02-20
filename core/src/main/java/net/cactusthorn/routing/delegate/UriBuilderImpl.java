package net.cactusthorn.routing.delegate;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import net.cactusthorn.routing.uri.Template;
import net.cactusthorn.routing.uri.UriComponentEncoder;
import net.cactusthorn.routing.uri.UriTemplate;

public class UriBuilderImpl extends UriBuilder implements Cloneable {

    private boolean opaque;
    private String scheme;
    private String schemeSpecificPart;

    private String authority;
    private String userInfo;
    private String host;
    private String port;

    private String path;
    private String query;

    private String fragment;

    @Override public UriBuilder clone() {
        UriBuilderImpl builder = new UriBuilderImpl();
        builder.opaque = opaque;
        builder.scheme = scheme;
        builder.schemeSpecificPart = schemeSpecificPart;
        builder.authority = authority;
        builder.userInfo = userInfo;
        builder.host = host;
        builder.port = port;
        builder.path = path;
        builder.query = query;
        builder.fragment = fragment;
        return builder;
    }

    /**
     * Copies the non-null components of the supplied URI to the UriBuilder
     * replacing any existing values for those components.
     */
    @Override public UriBuilder uri(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        if (uri.getScheme() != null) {
            scheme = uri.getScheme();
        }
        if (uri.getRawFragment() != null) {
            fragment = uri.getRawFragment();
        }
        if (uri.isOpaque()) {
            opaque = true;
            schemeSpecificPart = uri.getRawSchemeSpecificPart();
            authority = null;
            userInfo = null;
            host = null;
            port = null;
            path = null;
            query = null;
        } else {
            opaque = false;
            schemeSpecificPart = null;
            if (uri.getRawAuthority() != null) {
                if (uri.getUserInfo() == null && uri.getHost() == null && uri.getPort() == -1) {
                    authority = uri.getRawAuthority();
                    userInfo = null;
                    host = null;
                    port = null;
                } else {
                    authority = null;
                    if (uri.getRawUserInfo() != null) {
                        userInfo = uri.getRawUserInfo();
                    }
                    if (uri.getHost() != null) {
                        host = uri.getHost();
                    }
                    if (uri.getPort() != -1) {
                        port = String.valueOf(uri.getPort());
                    }
                }
            }
            if (!uri.getRawPath().isEmpty()) {
                path = uri.getRawPath();
            }
            if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
                query = uri.getRawQuery();
            }
        }
        return this;
    }

    /**
     * Parses the {@code uriTemplate} string and copies the parsed components of the
     * supplied URI to the UriBuilder replacing any existing values for those
     * components.
     */
    @Override public UriBuilder uri(String uriTemplate) {
        if (uriTemplate == null) {
            throw new IllegalArgumentException("uriTemplate is null");
        }
        UriTemplate template = new UriTemplate(uriTemplate);
        opaque = template.opaque();
        scheme = template.scheme();
        schemeSpecificPart = template.schemeSpecificPart();
        authority = template.authority();
        userInfo = template.userInfo();
        host = template.host();
        port = template.port();
        path = template.path();
        query = template.query();
        fragment = template.fragment();
        return this;
    }

    @Override public UriBuilder scheme(String s) {
        if (s == null) {
            scheme = null;
        } else {
            scheme =  new Template(s).template();
        }
        return this;
    }

    /**
     * Set the URI scheme-specific-part (see {@link java.net.URI}). This method will
     * overwrite any existing values for authority, user-info, host, port and path.
     */
    @Override public UriBuilder schemeSpecificPart(String ssp) {
        if (ssp == null) {
            throw new IllegalArgumentException("ssp is null");
        }
        UriTemplate template = new UriTemplate(scheme != null ? scheme + ':' + ssp : ssp);
        if (template.fragment() != null) {
            throw new IllegalArgumentException("schemeSpecificPart must not contain fragment");
        }
        opaque = template.opaque();
        if (opaque) {
            schemeSpecificPart = template.schemeSpecificPart();
            authority = null;
            userInfo = null;
            host = null;
            port = null;
            path = null;
            query = null;
        } else {
            schemeSpecificPart = null;
            authority = template.authority();
            userInfo = template.userInfo();
            host = template.host();
            port = template.port();
            path = template.path();
            query = template.query();
        }
        return this;
    }

    @Override public UriBuilder userInfo(String ui) {
        if (opaque) {
            throw new IllegalArgumentException("Can't set userInfo for opaque URI");
        }
        if (ui == null) {
            userInfo = null;
        } else {
            userInfo = new Template(UriComponentEncoder.USER_INFO.encode(ui)).template();
        }
        if (ui != null) {
            authority = null;
        }
        return this;
    }

    @Override public UriBuilder host(String h) {
        if (opaque) {
            throw new IllegalArgumentException("Can't set host for opaque URI");
        }
        if (h == null) {
            host = null;
        } else {
            host =  new Template(h).template();
        }
        if (h != null) {
            authority = null;
        }
        return this;
    }

    @Override public UriBuilder port(int p) {
        if (opaque) {
            throw new IllegalArgumentException("Can't set port for opaque URI");
        }
        if (p < -1) {
            throw new IllegalArgumentException("Invalid port");
        }
        if (p != -1) {
            authority = null;
            port = String.valueOf(p);
        } else {
            port = null;
        }
        return this;
    }

    @Override public UriBuilder replacePath(String p) {
        if (opaque) {
            throw new IllegalArgumentException("Can't replace Path for opaque URI");
        }
        if (p == null) {
            path = null;
        } else {
            path = new Template(UriComponentEncoder.PATH.encode(p)).template();
        }
        return this;
    }

    @Override public UriBuilder path(String p) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Path for opaque URI");
        }
        if (p == null) {
            throw new IllegalArgumentException("path is null");
        }
        return appendPath(UriComponentEncoder.PATH.encode(p));
    }

    @Override public UriBuilder path(@SuppressWarnings("rawtypes") Class resource) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Path for opaque URI");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource is null");
        }
        @SuppressWarnings("unchecked") Path annotation = (Path) resource.getAnnotation(Path.class);
        if (annotation == null) {
            throw new IllegalArgumentException("resource do not has @Path annotation");
        }
        return appendPath(UriComponentEncoder.PATH.encode(annotation.value()));
    }

    @Override public UriBuilder path(@SuppressWarnings("rawtypes") Class resource, String method) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Path for opaque URI");
        }
        if (resource == null) {
            throw new IllegalArgumentException("resource is null");
        }
        if (method == null) {
            throw new IllegalArgumentException("method is null");
        }
        String pathValue = null;
        for (Method m : resource.getMethods()) {
            if (method.equals(m.getName())) {
                Path annotation = (Path) m.getAnnotation(Path.class);
                if (annotation != null) {
                    if (pathValue != null) {
                        throw new IllegalArgumentException("there is more than one variant of the method");
                    }
                    pathValue = annotation.value();
                }
            }
        }
        if (pathValue == null) {
            throw new IllegalArgumentException("method not exist");
        }
        return appendPath(UriComponentEncoder.PATH.encode(pathValue));
    }

    @Override public UriBuilder path(Method method) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Path for opaque URI");
        }
        if (method == null) {
            throw new IllegalArgumentException("method is null");
        }
        Path annotation = (Path) method.getAnnotation(Path.class);
        if (annotation == null) {
            throw new IllegalArgumentException("method do not has @Path annotation");
        }
        return appendPath(UriComponentEncoder.PATH.encode(annotation.value()));
    }

    @Override public UriBuilder segment(String... segments) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Path-segments for opaque URI");
        }
        if (segments == null) {
            throw new IllegalArgumentException("segments are null");
        }
        for (String segment : segments) {
            if (segment == null) {
                throw new IllegalArgumentException("segment is null");
            }
            appendPath(UriComponentEncoder.PATH_SEGMENT.encode(segment));
        }
        return this;
    }

    @Override public UriBuilder replaceMatrix(String matrix) {
        throw new UnsupportedOperationException();
    }

    @Override public UriBuilder matrixParam(String name, Object... values) {
        throw new UnsupportedOperationException();
    }

    @Override public UriBuilder replaceMatrixParam(String name, Object... values) {
        throw new UnsupportedOperationException();
    }

    @Override public UriBuilder replaceQuery(String q) {
        if (opaque) {
            throw new IllegalArgumentException("Can't replace Query for opaque URI");
        }
        if (q == null) {
            query = null;
        } else {
            query = new Template(UriComponentEncoder.QUERY.encode(q)).template();
        }
        return this;
    }

    @Override public UriBuilder queryParam(String name, Object... values) {
        if (opaque) {
            throw new IllegalArgumentException("Can't append Query param for opaque URI");
        }
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        if (values.length == 0) {
            return this;
        }
        String encodedName = new Template(UriComponentEncoder.QUERY_PARAM.encode(name)).template();
        concatQueryParam(encodedName, values);
        return this;
    }

    @Override public UriBuilder replaceQueryParam(String name, Object... values) {
        if (opaque) {
            throw new IllegalArgumentException("Can't replace Query param for opaque URI");
        }
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        String encodedName = new Template(UriComponentEncoder.QUERY_PARAM.encode(name)).template();
        removeQueryParam(encodedName);
        if (values == null || values.length == 0) {
            return this;
        }
        concatQueryParam(encodedName, values);
        return this;
    }

    @Override public UriBuilder fragment(String f) {
        if (f == null) {
            fragment = null;
        } else {
            fragment = new Template(UriComponentEncoder.FRAGMENT.encode(f)).template();
        }
        return this;
    }

    @Override public UriBuilder resolveTemplate(String name, Object value) {
        return resolve(name, value, false, false);
    }

    @Override public UriBuilder resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
        return resolve(name, value, encodeSlashInPath, false);
    }

    @Override public UriBuilder resolveTemplateFromEncoded(String name, Object value) {
        return resolve(name, value, false, true);
    }

    @Override public UriBuilder resolveTemplates(Map<String, Object> templateValues) {
        if (templateValues == null) {
            throw new IllegalArgumentException("templateValues is null");
        }
        return resolve(templateValues, false, false);
    }

    @Override public UriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath)
            throws IllegalArgumentException {
        if (templateValues == null) {
            throw new IllegalArgumentException("templateValues is null");
        }
        return resolve(templateValues, encodeSlashInPath, false);
    }

    @Override public UriBuilder resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
        if (templateValues == null) {
            throw new IllegalArgumentException("templateValues is null");
        }
        return resolve(templateValues, false, true);
    }

    @Override @SuppressWarnings("unchecked") public URI buildFromMap(Map<String, ?> values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        try {
            UriBuilder clonned = this.clone();
            clonned.resolveTemplates((Map<String, Object>) values);
            return clonned.build();
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override @SuppressWarnings("unchecked") public URI buildFromMap(Map<String, ?> values, boolean encodeSlashInPath)
            throws IllegalArgumentException, UriBuilderException {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        try {
            UriBuilder clonned = this.clone();
            clonned.resolveTemplates((Map<String, Object>) values, encodeSlashInPath);
            return clonned.build();
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override @SuppressWarnings("unchecked") public URI buildFromEncodedMap(Map<String, ?> values)
            throws IllegalArgumentException, UriBuilderException {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        try {
            UriBuilder clonned = this.clone();
            clonned.resolveTemplatesFromEncoded((Map<String, Object>) values);
            return clonned.build();
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override public URI build(Object... values) throws IllegalArgumentException, UriBuilderException {
        if (values != null && values.length != 0) {
            Map<String, Object> variables = buildMap(values);
            return buildFromMap(variables);
        }
        try {
            return new URI(toTemplate());
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override public URI build(Object[] values, boolean encodeSlashInPath) throws IllegalArgumentException, UriBuilderException {
        if (values != null && values.length != 0) {
            Map<String, Object> variables = buildMap(values);
            return buildFromMap(variables, encodeSlashInPath);
        }
        try {
            return new URI(toTemplate());
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override public URI buildFromEncoded(Object... values) throws IllegalArgumentException, UriBuilderException {
        if (values != null && values.length != 0) {
            Map<String, Object> variables = buildMap(values);
            return buildFromEncodedMap(variables);
        }
        try {
            return new URI(toTemplate());
        } catch (Exception e) {
            throw new UriBuilderException(e);
        }
    }

    @Override public String toTemplate() {
        StringBuilder buf = new StringBuilder();
        if (scheme != null) {
            buf.append(scheme).append(':');
        }
        if (opaque) {
            buf.append(schemeSpecificPart);
        } else {
            if (authority != null) {
                buf.append("//").append(authority);
            } else if (userInfo != null || host != null || port != null) {
                buf.append("//");
                if (userInfo != null) {
                    buf.append(userInfo).append('@');
                }
                if (host != null) {
                    buf.append(host);
                }
                if (port != null) {
                    buf.append(':').append(port);
                }
            }
            if (path != null) {
                if (buf.length() != 0 && !path.isEmpty() && !path.startsWith("/")) {
                    buf.append("/");
                }
                buf.append(path);
            }
            if (query != null) {
                buf.append('?').append(query);
            }
        }
        if (fragment != null) {
            buf.append('#').append(fragment);
        }
        return buf.toString();
    }

    private Map<String, Object> buildMap(Object[] values) {
        Template template = new Template(toTemplate());
        Map<String, Object> variables = new HashMap<>();
        int position = -1;
        for (Template.TemplateVariable var : template.variables()) {
            if (variables.containsKey(var.name())) {
                continue;
            }
            position++;
            if (position >= values.length) {
                throw new IllegalArgumentException("Not enough values  for template variables");
            }
            if (values[position] == null) {
                throw new IllegalArgumentException("Null value is not allowed");
            }
            variables.put(var.name(), values[position]);
        }
        return variables;
    }

    private UriBuilder resolve(Map<String, Object> templateValues, boolean encodeSlashInPath, boolean fromEncoded) {
        for (Map.Entry<String, Object> entry : templateValues.entrySet()) {
            resolve(entry.getKey(), entry.getValue(), encodeSlashInPath, fromEncoded);
        }
        return this;
    }

    private UriBuilder resolve(String name, Object value, boolean encodeSlashInPath, boolean fromEncoded) {
        if (name == null) {
            throw new IllegalArgumentException("template variable name can't be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("template variable value can't be null");
        }
        String variable = '{' + name + '}';
        if (scheme != null) {
            scheme = scheme.replace(variable, value.toString());
        }
        if (opaque) {
            schemeSpecificPart = schemeSpecificPart.replace(variable,
                    UriComponentEncoder.SCHEME_SPECIFIC_PART.encode(value.toString(), fromEncoded));
        } else {
            if (authority != null) {
                authority = authority.replace(variable, value.toString());
            } else {
                if (userInfo != null) {
                    userInfo = userInfo.replace(variable, UriComponentEncoder.USER_INFO.encode(value.toString(), fromEncoded));
                }
                if (host != null) {
                    host = host.replace(variable, value.toString());
                }
                if (port != null) {
                    port = port.replace(variable, value.toString());
                }
            }
            if (path != null) {
                String prepared;
                if (encodeSlashInPath) {
                    prepared = UriComponentEncoder.PATH_SEGMENT.encode(value.toString(), fromEncoded);
                } else {
                    prepared = UriComponentEncoder.PATH.encode(value.toString(), fromEncoded);
                }
                path = path.replace(variable, prepared);
            }
            if (query != null) {
                query = query.replace(variable, UriComponentEncoder.QUERY.encode(value.toString(), fromEncoded));
            }
        }
        if (fragment != null) {
            fragment = fragment.replace(variable, UriComponentEncoder.FRAGMENT.encode(value.toString(), fromEncoded));
        }
        return this;
    }

    private void removeQueryParam(String encodedName) {
        StringJoiner joiner = new StringJoiner("&");
        String[] params = query.split("&");
        for (String param : params) {
            if (!param.startsWith(encodedName + '=')) {
                joiner.add(param);
            }
        }
        query = joiner.toString();
    }

    private void concatQueryParam(String encodedName, Object... values) {
        StringBuilder buf = new StringBuilder();
        for (Object value : values) {
            if (value != null) {
                if (buf.length() != 0) {
                    buf.append('&');
                }
                String encodedValue = new Template(UriComponentEncoder.QUERY_PARAM.encode(value.toString())).template();
                buf.append(encodedName).append('=').append(encodedValue);
            }
        }
        if (buf.length() == 0) {
            return;
        }
        if (query != null) {
            buf.insert(0, '&').insert(0, query);
        }
        query = buf.toString();
    }

    private UriBuilder appendPath(String p) {
        String prepared = new Template(p).template();
        if (path == null) {
            path = prepared;
        } else if (path.endsWith("/") && prepared.startsWith("/")) {
            path += prepared.substring(1);
        } else if (path.endsWith("/") ^ prepared.startsWith("/")) {
            path += prepared;
        } else {
            path += '/' + prepared;
        }
        return this;
    }
}
