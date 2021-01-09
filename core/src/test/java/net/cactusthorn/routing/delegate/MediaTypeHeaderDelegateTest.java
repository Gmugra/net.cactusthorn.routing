package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MediaTypeHeaderDelegateTest {

    private final static MediaTypeHeaderDelegate DELEGATE = new MediaTypeHeaderDelegate();

    @Test //
    public void wrongParam() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("aa/bb;w"));
    }

    @Test //
    public void fromStringNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }

    @Test //
    public void toStringNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }

    @Test //
    public void testCacheClear() {
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        while (strings.size() < MediaTypeHeaderDelegate.MAX_CACHE_SIZE + 1) {
            strings.add(random.ints('a', 'z' + 1).limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString());
        }
        strings.forEach(s -> DELEGATE.fromString(s));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void testIt(String header, MediaType expected) {
        MediaType mediaType = DELEGATE.fromString(header);
        assertEquals(expected, mediaType);
    }

    private static Stream<Arguments> provideArguments() {

        Map<String, String> simple = new LinkedHashMap<>();
        simple.put("q", "0.7");

        Map<String, String> two = new LinkedHashMap<>();
        two.put("q", "0.7");
        two.put("test", "testvalue");

        Map<String, String> quote = new LinkedHashMap<>();
        quote.put("quote", "ABC");

        Map<String, String> complex = new LinkedHashMap<>();
        complex.put("q", "0.7");
        complex.put("quote", "A\"B\"C");

        // @formatter:off
        return Stream.of(
            Arguments.of("aaa/vvvv", new MediaType("aaa", "vvvv")),
            Arguments.of("aaa/vvvv", new MediaType("aaa", "vvvv", (Map<String,String>)null)),
            Arguments.of("aaa", new MediaType("aaa", null)),
            Arguments.of("aaa/vvvv;q=0.7", new MediaType("aaa", "vvvv", simple)),
            Arguments.of("aaa/vvvv;quote=\"ABC\"", new MediaType("aaa","vvvv", quote)),
            Arguments.of("aaa/vvvv;q=0.7;quote=\"A\\\"B\\\"C\"", new MediaType("aaa", "vvvv", complex)),
            Arguments.of("aaa;q=0.7", new MediaType("aaa", null, simple)));
        // @formatter:on
    }
}
