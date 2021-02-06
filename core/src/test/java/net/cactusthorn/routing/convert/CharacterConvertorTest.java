package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CharacterConvertorTest {

    @Test //
    public void test() {
        CharacterConverter c = new CharacterConverter();
        Character result = c.convert(Character.class, null, null, "125");
        assertEquals('1', result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "");
        assertEquals(Character.MIN_VALUE, result);
    }

    @Test //
    public void testArray() throws Throwable {
        CharacterConverter c = new CharacterConverter();
        String[] value = new String[] { "215", "  ", "61" };
        Character[] valuesAsCharacter = new Character[] { '2', ' ', '6' };
        List<Character> result = c.convert(Character.class, null, null, value);
        assertArrayEquals(valuesAsCharacter, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        CharacterConverter c = new CharacterConverter();
        List<Character> result = c.convert(Character.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        CharacterConverter c = new CharacterConverter();
        List<Character> result = c.convert(Character.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
