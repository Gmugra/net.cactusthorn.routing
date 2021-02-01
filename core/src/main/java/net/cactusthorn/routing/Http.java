package net.cactusthorn.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class Http {

    public static final Comparator<MediaType> ACCEPT_COMPARATOR = (o1, o2) -> {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }

        if (o1.isWildcardType() && !o2.isWildcardType()) {
            return 1;
        }
        if (!o1.isWildcardType() && o2.isWildcardType()) {
            return -1;
        }
        if (o1.isWildcardSubtype() && !o2.isWildcardSubtype()) {
            return 1;
        }
        if (!o1.isWildcardSubtype() && o2.isWildcardSubtype()) {
            return -1;
        }

        double q1 = getQ(o1);
        double q2 = getQ(o2);
        if (q1 > q2) {
            return -1;
        }
        if (q2 > q1) {
            return 1;
        }
        return 0;
    };

    private static double getQ(MediaType mediaType) {
        String q = mediaType.getParameters().get("q");
        if (q == null) {
            return 1d;
        }
        return Double.parseDouble(q);
    }

    @SuppressWarnings("unchecked") //
    public static void writeHeaders(HttpServletResponse response, MultivaluedMap<String, Object> headers) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            String name = entry.getKey();
            for (Object header : entry.getValue()) {
                if (header == null) {
                    continue;
                }
                @SuppressWarnings("rawtypes") //
                HeaderDelegate headerDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(header.getClass());
                if (headerDelegate != null) {
                    response.addHeader(name, headerDelegate.toString(header));
                } else {
                    response.addHeader(name, header.toString());
                }
            }
        }
    }

    public static List<MediaType> parseAccept(Enumeration<String> acceptHeader) {
        List<MediaType> mediaTypes = new ArrayList<>();
        for (Enumeration<String> e = acceptHeader; e.hasMoreElements();) {
            String header = e.nextElement();
            String[] parts = header.split(",");
            for (String part : parts) {
                mediaTypes.add(MediaType.valueOf(part));
            }
        }
        if (mediaTypes.isEmpty()) {
            mediaTypes.add(MediaType.WILDCARD_TYPE);
        } else {
            Collections.sort(mediaTypes, ACCEPT_COMPARATOR);
        }
        return mediaTypes;
    }

    //RFC 2109
    public static List<Cookie> parseCookies(String cookieHeader) {
        List<Cookie> result = new ArrayList<>();

        int version = 0;
        String cookieName = null;
        String cookieValue = null;
        String domain = null;
        String path = null;

        String[] parts = cookieHeader.split("[;,]");
        for (String part : parts) {

            String[] subPart = getSubParts(part);
            String name = subPart[0];
            String value = subPart[1];

            if (!name.startsWith("$")) {
                if (cookieName != null) {
                    result.add(new Cookie(cookieName, cookieValue, path, domain, version));
                }
                cookieName = name;
                cookieValue = value;
                domain = null;
                path = null;
            } else if (name.startsWith("$Version")) {
                version = Integer.parseInt(value);
            } else if (name.startsWith("$Path")) {
                path = value;
            } else if (name.startsWith("$Domain")) {
                domain = value;
            }
        }
        result.add(new Cookie(cookieName, cookieValue, path, domain, version));
        return result;
    }

    private static String[] getSubParts(String str) {
        int valueStart = str.indexOf('=');
        if (valueStart == -1) {
            throw new IllegalArgumentException("Wrong: '=' is missing");
        }
        String value = str.substring(valueStart + 1).trim();
        if (value.charAt(0) == '"') {
            value = value.substring(1, value.length() - 1).trim();
        }
        return new String[] {str.substring(0, valueStart).trim(), value};
    }
}
