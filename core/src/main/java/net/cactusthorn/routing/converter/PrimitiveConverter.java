package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrimitiveConverter implements Converter<Object> {

    private Class<?> type;

    public PrimitiveConverter(Class<?> type) {
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("Not a primitive type");
        }
        this.type = type;
    }

    @Override //
    public Object convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        if (Byte.TYPE == type) {
            return convertByte(input);
        }
        if (Short.TYPE == type) {
            return convertShort(input);
        }
        if (Integer.TYPE == type) {
            return convertInt(input);
        }
        if (Long.TYPE == type) {
            return convertLong(input);
        }
        if (Float.TYPE == type) {
            return convertFloat(input);
        }
        if (Double.TYPE == type) {
            return convertDouble(input);
        }
        if (Character.TYPE == type) {
            return convertChar(input);
        }
        if (Boolean.TYPE == type) {
            return convertBoolean(input);
        }
        return null;
    }

    private static Integer convertInt(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Integer(0);
        }
        return Integer.valueOf(input);
    }

    private static Byte convertByte(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Byte((byte) 0);
        }
        return Byte.valueOf(input);
    }

    private static Short convertShort(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Short((short) 0);
        }
        return Short.valueOf(input);
    }

    private static Long convertLong(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Long(0L);
        }
        return Long.valueOf(input);
    }

    private static Float convertFloat(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Float(0.0f);
        }
        return Float.valueOf(input);
    }

    private static Double convertDouble(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Double(0.0d);
        }
        return Double.valueOf(input);
    }

    private static Character convertChar(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new Character('\u0000');
        }
        return Character.valueOf(input.charAt(0));
    }

    private static Boolean convertBoolean(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(input);
    }
}
