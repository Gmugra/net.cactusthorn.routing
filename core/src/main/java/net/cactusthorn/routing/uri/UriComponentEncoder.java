package net.cactusthorn.routing.uri;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public enum UriComponentEncoder {

    // https://tools.ietf.org/html/rfc3986

    SCHEME_SPECIFIC_PART(Characters.UNRESERVED, Characters.SUB_DELIMS, ":", "@"),
    USER_INFO(Characters.UNRESERVED, Characters.SUB_DELIMS, ":"),
    PATH(Characters.UNRESERVED, Characters.SUB_DELIMS, ":", "@", "/"),
    PATH_SEGMENT(Characters.UNRESERVED, Characters.SUB_DELIMS, ":", "@"),
    QUERY(Characters.UNRESERVED, Characters.SUB_DELIMS, ":", "@", "/", "?"),
    QUERY_PARAM(Characters.UNRESERVED, "!", "$", "'", "(", ")", "*", "+", ",", ";", ":", "@", "/", "?"),
    FRAGMENT(Characters.UNRESERVED, Characters.SUB_DELIMS, ":", "@", "/", "?");

    private static final class Characters {
        private static final String[] UNRESERVED = {"A-Z", "a-z", "0-9", "-", ".", "_", "~"};
        private static final String[] SUB_DELIMS = {"!", "$", "&", "'", "(", ")", "*", "+", ",", ";", "="};
    }

    private static final int HEX_RADIX = 16;
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final byte FOUR = 4;
    private static final byte OCTET_MASK = 0x0F;
    private static final int BYTE_MASK = 0xFF;

    private final boolean[] valid = new boolean[Byte.MAX_VALUE + 1];

    UriComponentEncoder(Object... objects) {
        for (Object characters : objects) {
            if (characters.getClass().isArray()) {
                for (String pattern : (String[]) characters) {
                    fillValid(pattern);
                }
            } else {
                fillValid((String) characters);
            }
        }
    }

    public String encode(String str) {
        return encode(str, true, true);
    }

    public String encode(String str, boolean fromEncoded) {
        return encode(str, false, fromEncoded);
    }

    public String encode(String str, boolean skipTemplates, boolean fromEncoded) {
        if (str == null) {
            return null;
        }
        boolean variable = false;
        StringBuilder result = new StringBuilder(str.length());
        for (int pos = 0; pos < str.length(); pos++) {
            int codePoint = str.codePointAt(pos);
            if (codePoint <= Byte.MAX_VALUE && valid[codePoint]) {
                result.append((char) codePoint);
                continue;
            }
            if (skipTemplates) {
                if (codePoint == '{') {
                    variable = true;
                    result.append('{');
                    continue;
                }
                if (codePoint == '}') {
                    variable = false;
                    result.append('}');
                    continue;
                }
                if (variable) {
                    result.append(Character.toChars(codePoint));
                    continue;
                }
            }
            if (fromEncoded && codePoint == '%' && pos + 2 < str.length()
                    && Character.digit(str.codePointAt(pos + 1), HEX_RADIX) != -1
                    && Character.digit(str.codePointAt(pos + 2), HEX_RADIX) != -1) {
                result.append('%').append(str.charAt(pos + 1)).append(str.charAt(pos + 2));
                pos += 2;
                continue;
            }
            if (codePoint <= Byte.MAX_VALUE) {
                appendByte(result, codePoint);
            } else {
                appendUTF8Char(result, codePoint);
            }
        }
        return result.toString();
    }

    private void fillValid(String pattern) {
        if (pattern.length() == 1) {
            valid[pattern.charAt(0)] = true;
        } else {
            for (char i = pattern.charAt(0); i <= pattern.charAt(2); i++) {
                valid[i] = true;
            }
        }
    }

    private static void appendByte(StringBuilder result, int b) {
        result.append('%');
        result.append(HEX_DIGITS[b >> FOUR]);
        result.append(HEX_DIGITS[b & OCTET_MASK]);
    }

    private static void appendUTF8Char(final StringBuilder sb,  int codePoint) {
        ByteBuffer bytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(Character.toChars(codePoint)));
        while (bytes.hasRemaining()) {
            appendByte(sb, bytes.get() & BYTE_MASK);
        }
    }
}
