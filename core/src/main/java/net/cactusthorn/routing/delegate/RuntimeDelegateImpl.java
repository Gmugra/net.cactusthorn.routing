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
        // TODO Auto-generated method stub
        return null;
    }

    @Override //
    public ResponseBuilder createResponseBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override //
    public VariantListBuilder createVariantListBuilder() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

}
