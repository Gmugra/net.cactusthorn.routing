package net.cactusthorn.routing.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperWrapper<E extends Throwable> extends Prioritised implements ExceptionMapper<E> {

    private Type throwable;
    private ExceptionMapper<E> mapper;

    public ExceptionMapperWrapper(ExceptionMapper<E> mapper) {
        super(mapper.getClass());
        this.mapper = mapper;
        findThrowable(mapper.getClass());
    }

    public Type throwable() {
        return throwable;
    }

    @Override public Response toResponse(E exception) {
        return mapper.toResponse(exception);
    }

    @SuppressWarnings("unchecked") public Response response(Throwable exception) {
        return mapper.toResponse((E) exception);
    }

    private void findThrowable(Class<?> clazz) {
        Class<?> current = clazz;
        do {
            Type t = getThrowable(current);
            if (t != null) {
                throwable = t;
                break;
            }
            current = current.getSuperclass();
        } while (current != null);
    }

    private Type getThrowable(Class<?> clazz) {
        Type[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == ExceptionMapper.class) {
                return ((ParameterizedType) clazz.getGenericInterfaces()[i]).getActualTypeArguments()[0];
            }
        }
        return null;
    }
}
