package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.validate.ParametersValidationException;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingConfigTest {

    public static final Converter TEST_CONVERTER = (req, type, value) -> {
        return new java.util.Date();
    };

    public static final Producer TEST_PRODUCER = (object, template, mediaType, req, resp) -> {
        return;
    };

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, req) -> {
        return null;
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

    @Test //
    public void converter() throws ConverterException, ParametersValidationException {

        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addEntryPoint(EntryPointDate.class)
                .addConverter(java.util.Date.class, TEST_CONVERTER).build();

        EntryPointScanner scanner = new EntryPointScanner(config);
        EntryPoint entryPoint = scanner.scan().get(GET.class).get(0);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        java.util.Date date = (java.util.Date) entryPoint.invoke(request, null, null, PathValues.EMPTY);
        assertNotNull(date);
    }

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
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addProducer("*/*", TEST_PRODUCER).build();
        Producer producer = config.producers().get("*/*");
        assertEquals(TEST_PRODUCER.getClass(), producer.getClass());
    }

    @Test //
    public void consumer() {
        RoutingConfig.builder(new EntryPointDateProvider()).addConsumer("aa/bb", TEST_CONSUMER).build();
    }

    @Test //
    public void responseCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setResponseCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test //
    public void readBodyBufferSize() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setReadBodyBufferSize(512).build();
        int value = (int) config.properties().get(ConfigProperty.READ_BODY_BUFFER_SIZE);
        assertEquals(512, value);
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
