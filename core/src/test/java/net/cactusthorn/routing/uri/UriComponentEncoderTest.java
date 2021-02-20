package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UriComponentEncoderTest {

    @Test public void strNull() {
        assertNull(UriComponentEncoder.SCHEME_SPECIFIC_PART.encode(null));
    }

    @Test public void skipTemplateVariables() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("v@vvvÜ{aÜb} dd");
        assertEquals("v@vvv%C3%9C{aÜb}%20dd", result);
    }

    @Test public void notSkipTemplateVariables() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("v@vvvÜ{aÜb} dd", false);
        assertEquals("v@vvv%C3%9C%7Ba%C3%9Cb%7D%20dd", result);
    }

    @Test public void alreadyEncoded() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("a%2F b");
        assertEquals("a%2F%20b", result);
    }

    @Test public void percent() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("ab%F");
        assertEquals("ab%25F", result);
    }

    @Test public void percent2() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("ab%2ZW");
        assertEquals("ab%252ZW", result);
    }

    @Test public void percent3() {
        String result = UriComponentEncoder.SCHEME_SPECIFIC_PART.encode("ab%ZAW");
        assertEquals("ab%25ZAW", result);
    }
}
