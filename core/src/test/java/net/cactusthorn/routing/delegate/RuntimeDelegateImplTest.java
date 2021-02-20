package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;

import org.junit.jupiter.api.Test;

public class RuntimeDelegateImplTest {

    private final static RuntimeDelegateImpl IMPL = new RuntimeDelegateImpl();

    @Test //
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> IMPL.createHeaderDelegate(null));
    }

    @Test //
    public void createResponseBuilder() {
        Response.ResponseBuilder builder = IMPL.createResponseBuilder();
        assertNotNull(builder);
    }

    @Test //
    public void createVariantListBuilder() {
        Variant.VariantListBuilder builder = IMPL.createVariantListBuilder();
        assertNotNull(builder);
    }

    @Test //
    public void createLinkBuilder() {
        Link.Builder builder = IMPL.createLinkBuilder();
        assertNotNull(builder);
    }

    @Test //
    public void createUriBuilder() {
        UriBuilder uriBuilder = IMPL.createUriBuilder();
        assertNotNull(uriBuilder);
    }

    @Test //
    public void checkUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createEndpoint(null, null));
    }
}
