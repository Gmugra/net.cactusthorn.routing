package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

public class ConvertersHolderTest {

    public static final Converter TEST_CONVERTER = (type, value) -> {
        return new java.util.Date();
    };

    @Test //
    public void register() throws Exception {
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
    public void valueOf() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(TestEnum.class).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(TestEnum.class, "AAAA"));
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
        Optional<Converter> c = holder.findConverter(Math.class);
        assertFalse(c.isPresent());
    }

    @Test //
    public void fromString() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(UUID.class).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
    }

    @Test //
    public void constructor() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(StringBuilder.class).get();
        assertEquals(StringConstructorConverter.class, converter.getClass());
    }
}
