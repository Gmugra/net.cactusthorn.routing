package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.converter.Converter;
import net.cactusthorn.routing.converter.ConverterException;

public class RoutingConfigTest {

    public static final Converter<java.util.Date> TEST_CONVERTER = (req, type, value) -> {
        return new java.util.Date();
    };

    public static final Producer TEST_PRODUCER = (object, mediaType, req, resp) -> {
        return;
    };

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, req) -> {
        return null;
    };

    public static class EntryPointWrong {

        @GET @Path("/dddd{/") //
        public void m4() {
        }
    }

    public static class EntryPointWrongProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPointWrong();
        }
    }

    @Test //
    public void exception() {
        assertThrows(RoutingException.class,
                () -> RoutingConfig.builder(new EntryPointWrongProvider()).addEntryPoint(EntryPointWrong.class).build());
    }

    public static class EntryPointDate {

        @GET @Path("/dddd{var}/") //
        public java.util.Date m4(@PathParam("var") java.util.Date date) {
            return date;
        }
    }

    public static class EntryPointDateProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPointDate();
        }
    }

    @Test //
    public void converter() throws ConverterException {
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addEntryPoint(Arrays.asList(EntryPointDate.class))
                .addConverter(java.util.Date.class, TEST_CONVERTER).build();

        EntryPoint entryPoint = config.entryPoints().get(GET.class).get(0);

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
        RoutingConfig config = RoutingConfig.builder(new EntryPointDateProvider()).addConsumer("*/*", TEST_CONSUMER).build();
        Consumer consumer = config.consumers().get("*/*");
        assertEquals(TEST_CONSUMER.getClass(), consumer.getClass());
    }
}
