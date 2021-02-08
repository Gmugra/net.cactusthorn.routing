package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CharacterConvertorTest {

    static final CharacterConverter CONVERTER = new CharacterConverter();

    @Test //
    public void test() {
        Character result = CONVERTER.convert(Character.class, null, null, "125");
        assertEquals('1', result);
    }

    @Test //
    public void nul() throws Exception {
        Character result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Character result = CONVERTER.convert(null, null, null, "");
        assertEquals(Character.MIN_VALUE, result);
    }
}
