package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.body.reader.ConvertersMessageBodyReader;
import net.cactusthorn.routing.body.writer.ObjectMessageBodyWriter;
import net.cactusthorn.routing.convert.ParamConverterProviderWrapperTest;
import net.cactusthorn.routing.resource.ResourceScanner;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingConfigTest {

    private static final ParametersValidator TEST_VALIDATOR = (object, method, parameters) -> {
    };

    public static class TestExceptionMapper implements Cloneable, ExceptionMapper<UnsupportedOperationException> {
        @Override public Response toResponse(UnsupportedOperationException exception) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    public static class EntryPointDate {

        @GET @Path("/dddd{var}/") //
        public String m4(@PathParam("var") String in) {
            return in;
        }
    }

    public static class EntryPointDateProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPointDate();
        }
    }

    @Test //
    public void paramConverterProvider() {

        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addResource(EntryPointDate.class)
                .addParamConverterProvider(new ParamConverterProviderWrapperTest.DefaultPriority()).build();

        ResourceScanner scanner = new ResourceScanner(config);
        ResourceScanner.Resource resource = scanner.scan().get(HttpMethod.GET).get(0);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        PathValues pathValues = new PathValues("var", "?");

        Response response = resource.invoke(request, null, null, pathValues);
        assertEquals("DefaultPriority", response.getEntity());
    }

    @Test //
    public void paramConverterProviderNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addParamConverterProvider(null));
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
    public void bodyWriter() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addBodyWriter(new ObjectMessageBodyWriter()).build();
        assertEquals(3, config.bodyWriters().size());
    }

    @Test //
    public void bodyReader() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addBodyReader(new ConvertersMessageBodyReader()).build();
        assertEquals(4, config.bodyReaders().size());
    }

    @Test //
    public void responseCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setResponseCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test //
    public void ioBufferSize() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setIOBufferSize(512).build();
        int bufferSize = (int) config.properties().get(ConfigProperty.IO_BUFFER_SIZE);
        assertEquals(512, bufferSize);
    }

    @Test //
    public void defaultIOBufferSize() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).build();
        int bufferSize = (int) config.properties().get(ConfigProperty.IO_BUFFER_SIZE);
        assertEquals(1024, bufferSize);
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

    @Test //
    public void exceptionMappers() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addExceptionMapper(new TestExceptionMapper()).build();
        assertEquals(UnsupportedOperationException.class, config.exceptionMappers().get(0).throwable());
    }
}
