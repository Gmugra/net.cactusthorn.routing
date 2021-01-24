package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class ConvertersHolderTest {

    public static class DateParamConverterProvider implements ParamConverterProvider {

        public static class DateParamConverter implements ParamConverter<java.util.Date> {

            @Override @SuppressWarnings("deprecation") public Date fromString(String value) {
                return new java.util.Date(70, 10, 10);
            }

            @Override public String toString(Date value) {
                return null;
            }
        }

        @Override @SuppressWarnings("unchecked") //
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if (rawType == java.util.Date.class) {
                return (ParamConverter<T>) new DateParamConverter();
            }
            return null;
        }
    }

    public static DateParamConverterProvider TEST_CONVERTER = new DateParamConverterProvider();

    @Test //
    public void register() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        holder.addProviders(Arrays.asList(new ParamConverterProvider[] {TEST_CONVERTER}));
        Converter converter = holder.findConverter(java.util.Date.class, null, null).get();
        java.util.Date date = (java.util.Date) converter.convert(java.util.Date.class, null, null, (String) null);
        assertNotNull(date);
    }

    public static enum TestEnum {
        AAAA, BBBB;
    }

    @Test //
    public void valueOf() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(TestEnum.class, null, null).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(TestEnum.class, null, null, "AAAA"));
    }

    @Test //
    public void valueOfAlreadyExist() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter1 = holder.findConverter(TestEnum.class, null, null).get();
        Converter converter2 = holder.findConverter(TestEnum.class, null, null).get();
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test //
    public void unknown() {
        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter> c = holder.findConverter(Math.class, null, null);
        assertFalse(c.isPresent());
    }

    @Test //
    public void fromString() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(UUID.class, null, null).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
    }

    @Test //
    public void constructor() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter converter = holder.findConverter(StringBuilder.class, null, null).get();
        assertEquals(StringConstructorConverter.class, converter.getClass());
    }
}
