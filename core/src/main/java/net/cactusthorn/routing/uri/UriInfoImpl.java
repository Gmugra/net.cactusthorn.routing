package net.cactusthorn.routing.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.UnmodifiableMultivaluedMap;

public class UriInfoImpl implements UriInfo {

    private final URI baseURI;
    private final URI absolutePath;
    private final URI requestURI;
    private final URI path;
    private final String queryString;
    private final PathValues pathValues;
    private MultivaluedMap<String, String> decodedQueryParameters;
    private MultivaluedMap<String, String> encodedQueryParameters;

    public UriInfoImpl(HttpServletRequest request, PathValues pathValues) {
        this.pathValues = pathValues;
        baseURI = createBaseURI(request);
        path = findPath(request);
        queryString = request.getQueryString();
        absolutePath = getBaseUri().resolve(getPath());
        if (queryString == null) {
            requestURI = absolutePath;
        } else {
            try {
                requestURI = new URI(absolutePath.getScheme(), absolutePath.getAuthority(), absolutePath.getPath(), queryString, null);
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }

        decodedQueryParameters = new MultivaluedHashMap<>();
        encodedQueryParameters = new MultivaluedHashMap<>();
        for (String parameter : Collections.list(request.getParameterNames())) {
            String[] values = request.getParameterValues(parameter);
            decodedQueryParameters.addAll(parameter, values);
            for (String value : values) {
                encodedQueryParameters.add(UriComponentEncoder.QUERY_PARAM.encode(parameter),
                        UriComponentEncoder.QUERY_PARAM.encode(value));
            }
        }
        decodedQueryParameters = new UnmodifiableMultivaluedMap<>(decodedQueryParameters);
        encodedQueryParameters = new UnmodifiableMultivaluedMap<>(encodedQueryParameters);
    }

    private static final String SCHEME_SEPARATOR = "://";

    // schema://authority/ContextPath/ServletPath/
    private URI createBaseURI(HttpServletRequest request) {
        try {
            String basePath = findBasePath(request);
            String xForwardedHost = request.getHeader("X-Forwarded-Host"); // X-Forwarded-Host: <host>
            if (xForwardedHost != null) {
                return new URI(findScheme(request) + SCHEME_SEPARATOR + xForwardedHost + basePath);
            }
            String hostHeader = request.getHeader(HttpHeaders.HOST); // Host: <host>:<port>
            if (hostHeader != null) {
                return new URI(findScheme(request) + SCHEME_SEPARATOR + hostHeader + basePath);
            }
            return new URI(findScheme(request), null, request.getLocalAddr(), request.getLocalPort(), basePath, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return /ContextPath/ServletPath/
     */
    private String findBasePath(HttpServletRequest request) {
        String result = request.getContextPath();
        if (!result.endsWith("/")) {
            result += '/';
        }
        if (!result.startsWith("/")) {
            result = '/' + result;
        }

        String servletPath = request.getServletPath();
        if (!"".equals(servletPath)) {
            result += servletPath.substring(1); // This path starts with a "/" character
            if (!result.endsWith("/")) {
                result += '/';
            }
        }
        return result;
    }

    private String findScheme(HttpServletRequest request) {
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null) {
            return xForwardedProto;
        }
        return request.isSecure() ? "https" : "http";
    }

    private URI findPath(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            return URI.create("");
        }
        if (pathInfo.startsWith("/")) {
            return URI.create(pathInfo.substring(1));
        }
        return URI.create(pathInfo);
    }

    /**
     * Get the path of the current request relative to the base URI as a string. All
     * sequences of escaped octets are decoded, equivalent to getPath(true).
     */
    @Override public String getPath() {
        return getPath(true);
    }

    @Override public String getPath(boolean decode) {
        return decode ? path.toString() : UriComponentEncoder.PATH.encode(path.toString());
    }

    @Override public List<PathSegment> getPathSegments() {
        throw new UnsupportedOperationException();
    }

    @Override public List<PathSegment> getPathSegments(boolean decode) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the absolute request URI including any query parameters.
     */
    @Override public URI getRequestUri() {
        return requestURI;
    }

    /**
     * Get the absolute request URI in the form of a UriBuilder.
     */
    @Override public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(getRequestUri());
    }

    /**
     * Get the absolute path of the request. This includes everything preceding the
     * path (host, port etc) but excludes query parameters. This is a shortcut for
     * uriInfo.getBaseUri().resolve(uriInfo.getPath(false)).
     */
    @Override public URI getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Get the absolute path of the request in the form of a UriBuilder. This
     * includes everything preceding the path (host, port etc) but excludes query
     * parameters.
     */
    @Override public UriBuilder getAbsolutePathBuilder() {
        return UriBuilder.fromUri(getAbsolutePath());
    }

    /**
     * Get the base URI of the application. URIs of root resource classes are all
     * relative to this base URI.
     */
    @Override public URI getBaseUri() {
        return baseURI;
    }

    /**
     * Get the base URI of the application in the form of a UriBuilder.
     */
    @Override public UriBuilder getBaseUriBuilder() {
        return UriBuilder.fromUri(getBaseUri());
    }

    @Override public MultivaluedMap<String, String> getPathParameters() {
        return getPathParameters(true);
    }

    @Override public MultivaluedMap<String, String> getPathParameters(boolean decode) {
        return pathValues.toMultivaluedMap(decode);
    }

    @Override public MultivaluedMap<String, String> getQueryParameters() {
        return getQueryParameters(true);
    }

    @Override public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
        if (decode) {
            return decodedQueryParameters;
        }
        return encodedQueryParameters;
    }

    @Override public List<String> getMatchedURIs() {
        throw new UnsupportedOperationException();
    }

    @Override public List<String> getMatchedURIs(boolean decode) {
        throw new UnsupportedOperationException();
    }

    @Override public List<Object> getMatchedResources() {
        throw new UnsupportedOperationException();
    }

    /**
     * Resolve a relative URI with respect to the base URI of the application. The
     * resolved URI returned by this method is normalized. If the supplied URI is
     * already resolved, it is just returned.
     */
    @Override public URI resolve(URI uri) {
        return getBaseUri().resolve(uri);
    }

    /**
     * Relativize a URI with respect to the current request URI. Relativization
     * works as follows:
     *
     * 1. If the URI to relativize is already relative, it is first resolved using
     * resolve(java.net.URI). 2. The resulting URI is relativized with respect to
     * the current request URI. If the two URIs do not share a prefix, the URI
     * computed in step 1 is returned.
     */
    @Override public URI relativize(URI uri) {
        URI to = uri;
        if (!to.isAbsolute()) {
            to = resolve(uri);
        }
        if (to.isOpaque() || !getBaseUri().getScheme().equals(to.getScheme()) || !getBaseUri().getAuthority().equals(to.getAuthority())) {
            return to;
        }

        String[] toSegments = pathSegments(to);
        String[] requestSegments = pathSegments(getRequestUri());

        UriBuilder builder = RuntimeDelegate.getInstance().createUriBuilder();
        builder.replaceQuery(to.getQuery());
        builder.fragment(to.getFragment());

        int position = 0;
        while (position < toSegments.length && position < requestSegments.length) {
            if (!toSegments[position].equals(requestSegments[position])) {
                break;
            }
            position++;
        }
        if (position < toSegments.length) {
            builder.segment(Arrays.copyOfRange(toSegments, position, toSegments.length));
        }

        return builder.build();
    }

    private String[] pathSegments(URI uri) {
        String uriPath = uri.getPath();
        if (uriPath.startsWith("/")) {
            uriPath = uriPath.substring(1);
        }
        return uriPath.split("/");
    }

}
