package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.junit.jupiter.api.Test;

public class ParamConverterProviderWrapperTest {

    public static class DefaultPriority implements ParamConverterProvider {

        public static class TestParamConverter implements ParamConverter<String> {

            private String test;

            public TestParamConverter(String test) {
                this.test = test;
            }

            @Override public String fromString(String value) {
                return test;
            }

            @Override public String toString(String value) {
                return null;
            }
        }

        @Override @SuppressWarnings("unchecked") //
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            if(rawType == java.util.Date.class) {
                return null;
            }
            return (ParamConverter<T>) new TestParamConverter(getClass().getSimpleName());
        }
    }

    @Priority(9999) public static class LowPriority extends DefaultPriority {
    }

    @Priority(50) public static class HiPriority extends DefaultPriority {
    }

    @Test // 
    public void sort() throws Exception {

        List<ParamConverterProviderWrapper> wrappers = new ArrayList<>();
        wrappers.add(new ParamConverterProviderWrapper(new DefaultPriority()));
        wrappers.add(null);
        wrappers.add(new ParamConverterProviderWrapper(new LowPriority()));
        wrappers.add(null);
        wrappers.add(new ParamConverterProviderWrapper(new HiPriority()));

        Collections.sort(wrappers, ParamConverterProviderWrapper.PRIORITY_COMPARATOR);

        assertEquals("HiPriority", wrappers.get(0).convert(null, null, null, (String)null));
        assertEquals("DefaultPriority", wrappers.get(1).convert(null, null, null, (String)null));
        assertEquals("LowPriority", wrappers.get(2).convert(null, null, null, (String)null));
        assertNull(wrappers.get(3));
        assertNull(wrappers.get(4));
    }

    @Test // 
    public void isConvertible() {
        ParamConverterProviderWrapper wrapper = new ParamConverterProviderWrapper(new DefaultPriority());
        assertTrue(wrapper.isConvertible(null, null, null));
        assertFalse(wrapper.isConvertible(java.util.Date.class, null, null));
    }
}
