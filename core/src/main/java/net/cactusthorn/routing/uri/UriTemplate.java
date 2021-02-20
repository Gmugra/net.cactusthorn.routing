package net.cactusthorn.routing.uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriTemplate extends Template {

    private static final String SCHEMA = "([^?#/]+):";
    private static final String SSP = "([^/#]{1}[^#]*)";
    private static final String AUTHORITY = "(//[^?#/]*)?";
    private static final String PATH = "([^#?]*)";
    private static final String QUERY = "(\\?([^#]*))?";
    private static final String FRAGMENT = "(#(.*))?";

    private static final int OPAQUE_SCHEMA_GROUP = 1;
    private static final int OPAQUE_SSP_GROUP = 2;
    private static final int OPAQUE_FRAGMENT_GROUP = 4;
    private static final Pattern OPAQUE_PATTERN = Pattern.compile("^" + SCHEMA + SSP + FRAGMENT);

    private static final int HIERARCHICAL_SCHEMA_GROUP = 2;
    private static final int HIERARCHICAL_AUTHORITY_GROUP = 3;
    private static final int HIERARCHICAL_PATH_GROUP = 4;
    private static final int HIERARCHICAL_QUERY_GROUP = 6;
    private static final int HIERARCHICAL_FRAGMENT_GROUP = 8;
    private static final Pattern HIERARCHICAL_PATTERN = Pattern.compile("^(" + SCHEMA + ")?" + AUTHORITY + PATH + QUERY + FRAGMENT);

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

    public UriTemplate(String template) {
        super(template);
        final Matcher opaqueMatcher = OPAQUE_PATTERN.matcher(template());
        if (opaqueMatcher.matches()) {
            opaque = true;
            scheme = opaqueMatcher.group(OPAQUE_SCHEMA_GROUP);
            schemeSpecificPart = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode(opaqueMatcher.group(OPAQUE_SSP_GROUP));
            fragment = UriComponentEncoder.FRAGMENT.encode(opaqueMatcher.group(OPAQUE_FRAGMENT_GROUP));
        } else {
            final Matcher hierarchicalMatcher = HIERARCHICAL_PATTERN.matcher(template());
            hierarchicalMatcher.matches();
            scheme = hierarchicalMatcher.group(HIERARCHICAL_SCHEMA_GROUP);
            parseAuthority(hierarchicalMatcher.group(HIERARCHICAL_AUTHORITY_GROUP));
            path = UriComponentEncoder.PATH.encode(hierarchicalMatcher.group(HIERARCHICAL_PATH_GROUP));
            query = UriComponentEncoder.QUERY.encode(hierarchicalMatcher.group(HIERARCHICAL_QUERY_GROUP));
            fragment = UriComponentEncoder.FRAGMENT.encode(hierarchicalMatcher.group(HIERARCHICAL_FRAGMENT_GROUP));
        }
    }

    public boolean opaque() {
        return opaque;
    }

    public String scheme() {
        return scheme;
    }

    public String schemeSpecificPart() {
        return schemeSpecificPart;
    }

    public String authority() {
        return authority;
    }

    public String userInfo() {
        return userInfo;
    }

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }

    public String path() {
        return path;
    }

    public String query() {
        return query;
    }

    public String fragment() {
        return fragment;
    }

    private void parseAuthority(String authorityGroup) {
        if (authorityGroup == null) {
            return;
        }
        authority = authorityGroup.substring(2);
        host = authority;
        final int at = authority.indexOf('@');
        if (at != -1) {
            userInfo = UriComponentEncoder.USER_INFO.encode(authority.substring(0, at));
            host = authority.substring(at + 1);
        }
        int portAt = host.lastIndexOf("]:"); // IP6
        if (portAt != -1) {
            port = host.substring(portAt + 2);
            host = host.substring(0, portAt + 1);
        } else {
            portAt = host.lastIndexOf(":"); // IP4
            if (portAt != -1) {
                port = host.substring(portAt + 1);
                host = host.substring(0, portAt);
            }
        }
        if ("".equals(userInfo)) {
            userInfo = null;
        }
        if ("".equals(host)) {
            host = null;
        }
        if ("".equals(port)) {
            port = null;
        }
        if (userInfo != null || host != null || port != null) {
            authority = null;
        }
    }
}
