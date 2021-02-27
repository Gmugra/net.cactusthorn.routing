package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.junit.jupiter.api.Test;

public class ExceptionMapperWrapperTest {

    public static class TestExceptionMapper implements Cloneable, ExceptionMapper<IllegalArgumentException> {
        @Override public Response toResponse(IllegalArgumentException exception) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    public static class TestExceptionMapper2 extends TestExceptionMapper {
        @Override public Response toResponse(IllegalArgumentException exception) {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }

    @Test public void simple() {
        ExceptionMapperWrapper<IllegalArgumentException> wrapper = new ExceptionMapperWrapper<>(new TestExceptionMapper());
        assertEquals(IllegalArgumentException.class, wrapper.throwable());
        assertEquals(Response.Status.CONFLICT.getStatusCode(), wrapper.toResponse(null).getStatus());
    }

    @Test public void superClass() {
        ExceptionMapperWrapper<IllegalArgumentException> wrapper = new ExceptionMapperWrapper<>(new TestExceptionMapper2());
        assertEquals(IllegalArgumentException.class, wrapper.throwable());
        assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(), wrapper.toResponse(null).getStatus());
    }
}
