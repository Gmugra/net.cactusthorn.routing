package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ConvertersHolderTest {

    public static final Converter<java.util.Date> TEST_CONVERTER = (req, res, con, value) -> {
        return new java.util.Date();
    };

    @Test //
    public void register() {
        ConvertersHolder holder = new ConvertersHolder();
        holder.register(java.util.Date.class, TEST_CONVERTER);
        Converter<?> converter = holder.findConverter(java.util.Date.class);
        java.util.Date date = (java.util.Date) converter.convert(null, null, null, null);
        assertNotNull(date);
    }

    public static enum TestEnum {
        AAAA, BBBB;
    }

    @Test //
    public void valueOf() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(TestEnum.class);
        assertEquals(ValueOfConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(null, null, null, "AAAA"));
    }

    @Test //
    public void valueOfAlreadyExist() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter1 = holder.findConverter(TestEnum.class);
        Converter<?> converter2 = holder.findConverter(TestEnum.class);
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test //
    public void valueOfAlreadyNull() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter1 = holder.findConverter(java.util.Date.class);
        Converter<?> converter2 = holder.findConverter(java.util.Date.class);
        assertEquals(NullConverter.class, converter1.getClass());
        assertEquals(NullConverter.class, converter2.getClass());
    }
}
