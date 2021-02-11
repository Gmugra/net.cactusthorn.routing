package net.cactusthorn.routing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

public final class Headers {

    public static final class AcceptComparator implements Comparator<MediaType> {

        @Override //
        public int compare(MediaType o1, MediaType o2) {
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
        }

        private double getQ(MediaType mediaType) {
            String q = mediaType.getParameters().get("q");
            if (q == null) {
                return 1d;
            }
            return Double.parseDouble(q);
        }
    };

    public static final class AcceptLanguageComparator implements Comparator<Language> {

        @Override //
        public int compare(Language o1, Language o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
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
        }

        private double getQ(Language language) {
            String q = language.getQ();
            return Double.parseDouble(q);
        }
    };

    public static final Comparator<MediaType> ACCEPT_COMPARATOR = new AcceptComparator();

    public static final Comparator<Language> ACCEPT_LANGUAGE_COMPARATOR = new AcceptLanguageComparator();

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

    // RFC 2109
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

    public static List<MediaType> parseAccept(String acceptHeader) {
        List<MediaType> mediaTypes = new ArrayList<>();
        if (acceptHeader != null) {
            String[] parts = acceptHeader.split(",");
            for (String part : parts) {
                mediaTypes.add(MediaType.valueOf(part));
            }
        }
        if (mediaTypes.isEmpty()) {
            mediaTypes.add(MediaType.WILDCARD_TYPE);
        } else {
            Collections.sort(mediaTypes, ACCEPT_COMPARATOR);
        }
        return Collections.unmodifiableList(mediaTypes);
    }

    public static List<Locale> parseAcceptLanguage(String acceptLanguageHeader) {
        if (acceptLanguageHeader == null || acceptLanguageHeader.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Language> languages = new ArrayList<>();
        String[] parts = acceptLanguageHeader.split(",");
        for (String part : parts) {
            languages.add(Language.valueOf(part));
        }
        Collections.sort(languages, ACCEPT_LANGUAGE_COMPARATOR);
        List<Locale> locales = new ArrayList<>();
        for (Language language : languages) {
            locales.add(language.getLocale());
        }
        return Collections.unmodifiableList(locales);
    }
}
