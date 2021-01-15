package net.cactusthorn.routing.bodywriter;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaTypeAcceptComparatorTest {

    private static final MediaTypeAcceptComparator COMPARATOR = new MediaTypeAcceptComparator();

    @Test //
    public void sort() {

        List<MediaType> list = new ArrayList<>();
        list.add(new MediaType("test", "html", addQ("0.5")));
        list.add(MediaType.WILDCARD_TYPE);
        list.add(null);
        list.add(MediaType.TEXT_PLAIN_TYPE);
        list.add(new MediaType("application", "json", addQ("0.875")));
        list.add(new MediaType("test", "*"));
        list.add(new MediaType("application", "*"));
        list.add(new MediaType("*", "json"));
        list.add(null);

        Collections.sort(list, COMPARATOR);

        assertEquals("text/plain", _toString(list.get(0)));
        assertEquals("application/json", _toString(list.get(1)));
        assertEquals("test/html", _toString(list.get(2)));
        assertEquals("test/*", _toString(list.get(3)));
        assertEquals("application/*", _toString(list.get(4)));
        assertEquals("*/json", _toString(list.get(5)));
        assertEquals("*/*", _toString(list.get(6)));
        assertEquals("NULL", _toString(list.get(7)));
        assertEquals("NULL", _toString(list.get(8)));
    }

    private Map<String, String> addQ(String value) {
        Map<String, String> result = new HashMap<>();
        result.put("q", value);
        return result;
    }

    private String _toString(MediaType mediaType) {
        if (mediaType == null) {
            return "NULL";
        }
        return mediaType.getType() + '/' + mediaType.getSubtype();
    }
}
