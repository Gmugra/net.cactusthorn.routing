package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Consumer;

public class ConvertersHolderTest {

    public static final Converter<java.util.Date> TEST_CONVERTER = (req, type, value) -> {
        return new java.util.Date();
    };

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    @Test //
    public void register() throws ConverterException {
        ConvertersHolder holder = new ConvertersHolder();
        holder.register(java.util.Date.class, TEST_CONVERTER);
        Converter<?> converter = holder.findConverter(java.util.Date.class);
        java.util.Date date = (java.util.Date) converter.convert(null, null, null);
        assertNotNull(date);
    }

    public static enum TestEnum {
        AAAA, BBBB;
    }

    @Test //
    public void valueOf() throws ConverterException {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(TestEnum.class);
        assertEquals(ValueOfConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(null, TestEnum.class, "AAAA"));
    }

    @Test //
    public void valueOfAlreadyExist() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter1 = holder.findConverter(TestEnum.class);
        Converter<?> converter2 = holder.findConverter(TestEnum.class);
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test //
    public void unknown() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter1 = holder.findConverter(java.util.Date.class);
        assertEquals(NullConverter.class, converter1.getClass());
    }
    
    @Test //
    public void consumer() {
        ConvertersHolder holder = new ConvertersHolder();
        holder.register("a/b", TEST_CONSUMER);
        //?
    }
}
