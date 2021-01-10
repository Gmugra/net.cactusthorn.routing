package net.cactusthorn.routing.bodyreader;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class TextPlainBodyReaderTest {

    private static final ConvertersHolder HOLDER = new ConvertersHolder();

    @Test //
    public void isReadable() {
        TextPlainBodyReader bodyReader = new TextPlainBodyReader(HOLDER);
        assertFalse(bodyReader.isReadable(null, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }
}
