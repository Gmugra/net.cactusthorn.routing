package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class CharacterConvertorTest {

    @Test //
    public void test() throws Exception {
        CharacterConverter c = new CharacterConverter();
        Character result = (Character) c.convert(Character.class, "125");
        assertEquals('1', result);
        result = (Character) c.convert(null, null, (String) null);
        assertNull(result);
        result = (Character) c.convert(null, null, "");
        assertEquals(Character.MIN_VALUE, result);
    }

    @Test //
    public void testArray() throws Exception {
        CharacterConverter c = new CharacterConverter();
        String[] value = new String[] { "215", "  ", "61" };
        Character[] valuesAsCharacter = new Character[] { '2', ' ', '6' };
        Character[] result = (Character[]) c.convert(Character.class, value);
        assertArrayEquals(valuesAsCharacter, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        CharacterConverter c = new CharacterConverter();
        Object result = (Object) c.convert(Character.class, (String[]) null);
        assertNull(result);
    }
}
