package net.cactusthorn.routing.delegate;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

public class RuntimeDelegateImpl extends RuntimeDelegate {

    private final Map<Class<?>, HeaderDelegate<?>> headerDelegates = new HashMap<>();

    public RuntimeDelegateImpl() {
        headerDelegates.put(MediaType.class, new MediaTypeHeaderDelegate());
    }

    @Override //
    public UriBuilder createUriBuilder() {
        throw new UnsupportedOperationException("createUriBuilder() is not supported");
    }

    @Override //
    public ResponseBuilder createResponseBuilder() {
        throw new UnsupportedOperationException("createResponseBuilder() is not supported");
    }

    @Override //
    public VariantListBuilder createVariantListBuilder() {
        throw new UnsupportedOperationException("createVariantListBuilder() is not supported");
    }

    @Override //
    public <T> T createEndpoint(Application application, Class<T> endpointType)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createEndpoint() is not supported");
    }

    @Override @SuppressWarnings("unchecked") //
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        return (HeaderDelegate<T>) headerDelegates.get(type);
    }

    @Override //
    public Builder createLinkBuilder() {
        throw new UnsupportedOperationException("createLinkBuilder() is not supported");
    }

}
