package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.convert.ParamConverterProviderWrapperTest;
import net.cactusthorn.routing.resource.ResourceScanner;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingConfigTest {

    public static final class TestValidator implements ParametersValidator {
        @Override public void validate(Object object, Method method, Object[] parameters) {
        }
    }

    public static class TestExceptionMapper implements Cloneable, ExceptionMapper<UnsupportedOperationException> {
        @Override public Response toResponse(UnsupportedOperationException exception) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    public static class EntryPointDate {
        @GET @Path("/dddd{var}/") public String m4(@PathParam("var") String in) {
            return in;
        }
    }

    public static class EntryPointDateProvider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPointDate();
        }
    }

    @Test public void paramConverterProvider() {

        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addResource(EntryPointDate.class)
                .addParamConverterProvider(new ParamConverterProviderWrapperTest.DefaultPriority()).build();

        ResourceScanner scanner = new ResourceScanner(config);
        ResourceScanner.Resource resource = scanner.scan().get(HttpMethod.GET).get(0);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        PathValues pathValues = new PathValues("var", "?");

        Response response = resource.invoke(request, null, null, pathValues);
        assertEquals("DefaultPriority", response.getEntity());
    }

    @Test public void paramConverterProviderClass() {

        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addResource(EntryPointDate.class)
                .addParamConverterProvider(ParamConverterProviderWrapperTest.DefaultPriority.class).build();

        ResourceScanner scanner = new ResourceScanner(config);
        ResourceScanner.Resource resource = scanner.scan().get(HttpMethod.GET).get(0);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        PathValues pathValues = new PathValues("var", "?");

        Response response = resource.invoke(request, null, null, pathValues);
        assertEquals("DefaultPriority", response.getEntity());
    }

    @Test public void paramConverterProviderWrongClass() {
        RoutingConfig.Builder builder = RoutingConfig.builder(new EntryPointDateProvider());
        assertThrows(IllegalArgumentException.class, () -> builder.addParamConverterProvider(String.class));
    }

    @Test public void paramConverterProviderNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addParamConverterProvider((ParamConverterProvider) null));
    }

    @Test public void paramConverterProviderClassNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addParamConverterProvider((Class<ParamConverterProvider>) null));
    }

    @Test public void provider() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).build();
        ComponentProvider provider = config.provider();
        assertEquals(EntryPointDateProvider.class, provider.getClass());
    }

    @Test public void nullProvider() {
        assertThrows(IllegalArgumentException.class, () -> RoutingConfig.builder(null));
    }

    @Test public void responseCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setResponseCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test public void ioBufferSize() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setIOBufferSize(512).build();
        int bufferSize = (int) config.properties().get(ConfigProperty.IO_BUFFER_SIZE);
        assertEquals(512, bufferSize);
    }

    @Test public void defaultIOBufferSize() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).build();
        int bufferSize = (int) config.properties().get(ConfigProperty.IO_BUFFER_SIZE);
        assertEquals(1024, bufferSize);
    }

    @Test public void defaultRequestCharacterEncoding() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setDefaultRequestCharacterEncoding("KOI8-R").build();
        String value = (String) config.properties().get(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING);
        assertEquals("KOI8-R", value);
    }

    @Test public void parametersValidator() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setParametersValidator(new TestValidator()).build();
        Optional<ParametersValidator> validator = config.validator();
        assertTrue(validator.isPresent());
    }

    @Test public void parametersValidatorClass() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).setParametersValidator(TestValidator.class).build();
        Optional<ParametersValidator> validator = config.validator();
        assertTrue(validator.isPresent());
    }

    @Test public void parametersValidatorWrongClass() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).setParametersValidator(String.class));
    }

    public static class WrongResource {
        @Consumes(MediaType.WILDCARD) public void m4(String in) {
        }
    }

    public static class WrongResourceProvider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPointDate();
        }
    }

    @Test public void validateResource() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new WrongResourceProvider()).addResource(WrongResource.class).build());
    }

    @Test public void validateResources() {
        List<Class<?>> resources = Arrays.asList(WrongResource.class);
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new WrongResourceProvider()).addResource(resources).build());
    }

    @Test public void resourceNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addResource((Class<?>) null).build());
    }

    @Test public void resourcesNull() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addResource((Collection<Class<?>>) null).build());
    }

    @Test public void putApplicationProperties() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).putApplicationProperties("P", "V").build();
        assertEquals("V", config.application().getProperties().get("P"));
    }

    @Test public void addExceptionMapperClass() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addExceptionMapper(TestExceptionMapper.class).build();
        assertEquals(TestExceptionMapper.class, config.application().getClasses().iterator().next());
    }

    @Test public void addExceptionMapperWrongClass() {
        RoutingConfig.Builder builder = RoutingConfig.builder(new EntryPointDateProvider());
        assertThrows(IllegalArgumentException.class, () -> builder.addExceptionMapper(String.class));
    }

    public static final class TestReader implements MessageBodyReader<Object> {
        @Override public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    }

    @Test public void addMessageBodyReaderClass() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addMessageBodyReader(TestReader.class).build();
        assertEquals(TestReader.class, config.application().getClasses().iterator().next());
    }

    @Test public void addMessageBodyReaderWrongClass() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addMessageBodyReader(String.class));
    }

    public static final class TestWriter implements MessageBodyWriter<Object> {
        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        }
    }

    @Test public void addMessageBodyWriterClass() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addMessageBodyWriter(TestWriter.class).build();
        assertEquals(TestWriter.class, config.application().getClasses().iterator().next());
    }

    @Test public void addBodyWriterWrongClass() {
        assertThrows(IllegalArgumentException.class,
                () -> RoutingConfig.builder(new EntryPointDateProvider()).addMessageBodyWriter(String.class));
    }
}
