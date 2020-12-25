package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Consumer;

import java.util.Optional;

public class ConvertersHolderTest {

    public static final Converter TEST_CONVERTER = (req, type, value) -> {
        return new java.util.Date();
    };

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    @Test //
    public void register() throws ConverterException {
        ConvertersHolder holder = new ConvertersHolder();
        holder.register(java.util.Date.class, TEST_CONVERTER);
        Converter converter = holder.findConverter(java.util.Date.class).get();
        java.util.Date date = (java.util.Date) converter.convert(java.util.Date.class, (String) null);
        assertNotNull(date);
    }

    public static enum TestEnum {
        AAAA, BBBB;
    }

    @Test //
    public void valueOf() throws ConverterException {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(TestEnum.class).get();
        assertEquals(ValueOfConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(null, TestEnum.class, "AAAA"));
    }

    @Test //
    public void valueOfAlreadyExist() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter1 = holder.findConverter(TestEnum.class).get();
        Converter converter2 = holder.findConverter(TestEnum.class).get();
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test //
    public void unknown() {
        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter> c = holder.findConverter(java.util.Date.class);
        assertFalse(c.isPresent());
    }

    @Test //
    public void consumer() {
        ConvertersHolder holder = new ConvertersHolder();
        holder.register("a/b", TEST_CONSUMER);
        // ?
    }

    @Test //
    public void consumerNotFound() {
        ConvertersHolder holder = new ConvertersHolder();
        Optional<ConsumerConverter> c = holder.findConsumerConverter("aa/bb");
        assertFalse(c.isPresent());
    }

    public static class X {
        public static X valueOf(String s) {
            return new X();
        }
    }

    @Test //
    public void byTypeNotFound() {
        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter> c = holder.findConverter(java.util.Date.class);
        assertFalse(c.isPresent());
    }
}
