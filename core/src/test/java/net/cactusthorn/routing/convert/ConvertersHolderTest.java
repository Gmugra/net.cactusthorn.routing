package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
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
        holder.addProviders(Arrays.asList(new ParamConverterProvider[] { TEST_CONVERTER }));
        Converter<?> converter = holder.findConverter(java.util.Date.class, null, null).get();
        java.util.Date date = (java.util.Date) converter.convert(java.util.Date.class, null, null, (String) null);
        assertNotNull(date);
    }

    public static enum TestEnum {
        AAAA, BBBB;
    }

    public static enum TestEnumFromString {
        AAAA, BBBB;

        public static TestEnumFromString fromString(String value) {
            return AAAA;
        }
    }
    
    public static class TestValueOf {

        private String value;

        public static TestValueOf valueOf(String value) {
            TestValueOf testValueOf = new TestValueOf();
            testValueOf.value = value;
            return testValueOf;
        }

        @Override public String toString() {
            return value;
        }
    }

    @Test //
    public void valueOf() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(TestValueOf.class, null, null).get();
        TestValueOf result = (TestValueOf) converter.convert(TestValueOf.class, null, null, "XYZ");
        assertEquals("XYZ", result.toString());
    }

    @Test //
    public void enumFromString() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(TestEnumFromString.class, null, null).get();
        TestEnumFromString result = (TestEnumFromString) converter.convert(TestEnumFromString.class, null, null, "CCCC");
        assertEquals(TestEnumFromString.AAAA, result);
    }

    @Test //
    public void enumValueOf() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(TestEnum.class, null, null).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
        assertEquals(TestEnum.AAAA, converter.convert(TestEnum.class, null, null, "AAAA"));
    }

    @Test //
    public void valueOfAlreadyExist() {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter1 = holder.findConverter(TestEnum.class, null, null).get();
        Converter<?> converter2 = holder.findConverter(TestEnum.class, null, null).get();
        assertEquals(converter1.hashCode(), converter2.hashCode());
    }

    @Test //
    public void unknown() {
        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> c = holder.findConverter(Math.class, null, null);
        assertFalse(c.isPresent());
    }

    @Test //
    public void fromString() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(UUID.class, null, null).get();
        assertEquals(StaticStringMethodConverter.class, converter.getClass());
    }

    @Test //
    public void constructor() throws Exception {
        ConvertersHolder holder = new ConvertersHolder();
        Converter<?> converter = holder.findConverter(StringBuilder.class, null, null).get();
        assertEquals(StringConstructorConverter.class, converter.getClass());
    }

    /**
     * According to JSR-339 simple arrays are not supported
     */
    @Test //
    public void simpleArray() {
        ConvertersHolder holder = new ConvertersHolder();
        String[] array = new String[0];
        Optional<Converter<?>> converter = holder.findConverter(array.getClass(), null, null);
        assertFalse(converter.isPresent());
    }

    public static class TestIt {

        public void set(Set<Integer> input) {
        }

        public void withoutGeneric(@SuppressWarnings("rawtypes") Set input) {
        }

        public void list(List<Double> input) {
        }

        public void sortedSet(SortedSet<UUID> input) {
        }

        public void collection(Collection<String> input) {
        }
    }

    @Test //
    public void set() {
        Method method = findMethod(TestIt.class, "set");
        Class<?> type = method.getParameters()[0].getType();
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> converter = holder.findConverter(type, genericType, null);
        assertEquals("IntegerConverter", converter.get().getClass().getSimpleName());
    }

    /**
     * According to JSR-339 collections without generic are not supported
     */
    @Test //
    public void withoutGeneric() {
        Method method = findMethod(TestIt.class, "withoutGeneric");
        Class<?> type = method.getParameters()[0].getType();
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> converter = holder.findConverter(type, genericType, null);
        assertFalse(converter.isPresent());
    }

    @Test //
    public void list() {
        Method method = findMethod(TestIt.class, "list");
        Class<?> type = method.getParameters()[0].getType();
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> converter = holder.findConverter(type, genericType, null);
        assertEquals("DoubleConverter", converter.get().getClass().getSimpleName());
    }

    @Test //
    public void sortedSet() {
        Method method = findMethod(TestIt.class, "sortedSet");
        Class<?> type = method.getParameters()[0].getType();
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> converter = holder.findConverter(type, genericType, null);
        assertEquals("StaticStringMethodConverter", converter.get().getClass().getSimpleName());
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>; nothing
     * else
     */
    @Test //
    public void collection() {
        Method method = findMethod(TestIt.class, "collection");
        Class<?> type = method.getParameters()[0].getType();
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        Optional<Converter<?>> converter = holder.findConverter(type, genericType, null);
        assertFalse(converter.isPresent());
    }

    protected Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
