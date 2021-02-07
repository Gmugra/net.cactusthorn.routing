package net.cactusthorn.routing.delegate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Cookie;

public class Headers {

    public static boolean containsWhiteSpace(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    public static String addQuotesIfContainsWhitespace(String str) {
        if (containsWhiteSpace(str)) {
            return '"' + str + '"';
        }
        return str;
    }

    public static void addQuotesIfContainsWhitespace(StringBuilder buf, String str) {
        if (containsWhiteSpace(str)) {
            buf.append('"').append(str).append('"');
        } else {
            buf.append(str);
        }
    }

    public static String[] getSubParts(String str) {
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

    public static List<Cookie> parseCookies(String cookieName, String cookieHeader) {
        List<Cookie> result = parseCookies(cookieHeader);
        return result.stream().filter(c -> c.getName().equals(cookieName)).collect(Collectors.toList());
    }
}
