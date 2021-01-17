package net.cactusthorn.routing.body.reader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class WildcardBodyReaderTest {

    public static class TestProvider implements ComponentProvider {
        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return null;
        }
    }

    @Test //
    public void isReadable() {
        RoutingConfig config = RoutingConfig.builder(new TestProvider()).build();
        WildcardMessageBodyReader bodyReader = new WildcardMessageBodyReader();
        bodyReader.init(null, config);
        assertTrue(bodyReader.isReadable(String.class, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        assertTrue(bodyReader.isReadable(BufferedReader.class, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        assertTrue(bodyReader.isReadable(InputStream.class, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }
}
