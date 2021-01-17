package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.body.reader.WildcardMessageBodyReader;
import net.cactusthorn.routing.body.writer.WildcardMessageBodyWriter;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingConfigTest {

    public static final Converter TEST_CONVERTER = (type, value) -> {
        return new java.util.Date();
    };

    private static final ParametersValidator TEST_VALIDATOR = (object, method, parameters) -> {
    };

    public static class EntryPointDate {

        @GET @Path("/dddd{var}/") //
        public java.util.Date m4(@PathParam("var") java.util.Date date) {
            return date;
        }
    }

    public static class EntryPointDateProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPointDate();
        }
    }

    // TODO FIX IT
    /*
     * @Test // public void converter() {
     * 
     * RoutingConfig config = RoutingConfig.builder(new
     * EntryPointDateProvider()).addEntryPoint(EntryPointDate.class)
     * .addConverter(java.util.Date.class, TEST_CONVERTER).build();
     * 
     * EntryPointScanner scanner = new EntryPointScanner(config); EntryPoint
     * entryPoint = scanner.scan().get(HttpMethod.GET).get(0);
     * 
     * HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
     * java.util.Date date = (java.util.Date) entryPoint.invoke(request, null, null,
     * PathValues.EMPTY); assertNotNull(date); }
     */

    @Test //
    public void provider() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).build();
        ComponentProvider provider = config.provider();
        assertEquals(EntryPointDateProvider.class, provider.getClass());
    }

    @Test //
    public void nullProvider() {
        assertThrows(IllegalArgumentException.class, () -> RoutingConfig.builder(null));
    }

    @Test //
    public void producer() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider())
                .addBodyWriter(MediaType.WILDCARD_TYPE, new WildcardMessageBodyWriter()).build();
        assertEquals(2, config.bodyWriters().size());
    }

    @Test //
    public void bodyReader() {
        MediaType aabb = new MediaType("aa", "bb");
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addBodyReader(aabb, new WildcardMessageBodyReader())
                .build();
        assertEquals(2, config.bodyReaders().size());
    }

    @Test //
    public void responseCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setResponseCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test //
    public void defaultRequestCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setDefaultRequestCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test //
    public void parametersValidator() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setParametersValidator(TEST_VALIDATOR).build();
        Optional<ParametersValidator> validator = config.validator();
        assertTrue(validator.isPresent());
    }

    @Test //
    public void applicationPath() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setApplicationPath("/yyyy").build();
        assertEquals("/yyyy/", config.applicationPath());
    }

    @Test //
    public void applicationPathNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).setApplicationPath(null).build());
    }

    @Test //
    public void applicationPathDefault() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setApplicationPath("/").build();
        assertEquals("/", config.applicationPath());
    }

    @Test //
    public void applicationPathAdd() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setApplicationPath("yy/cc").build();
        assertEquals("/yy/cc/", config.applicationPath());
    }

    @Test //
    public void applicationPathEnd() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setApplicationPath("yy/cc/").build();
        assertEquals("/yy/cc/", config.applicationPath());
    }
}
