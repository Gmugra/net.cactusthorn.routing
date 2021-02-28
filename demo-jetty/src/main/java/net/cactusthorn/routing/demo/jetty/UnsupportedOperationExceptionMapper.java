package net.cactusthorn.routing.demo.jetty;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {

    @Override public Response toResponse(UnsupportedOperationException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
}
