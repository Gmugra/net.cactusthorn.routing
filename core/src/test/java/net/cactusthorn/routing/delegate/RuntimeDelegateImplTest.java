package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RuntimeDelegateImplTest {

    private final static RuntimeDelegateImpl IMPL = new RuntimeDelegateImpl();

    @Test //
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> IMPL.createHeaderDelegate(null));
    }

    @Test //
    public void checkUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createUriBuilder());
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createResponseBuilder());
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createVariantListBuilder());
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createEndpoint(null, null));
        assertThrows(UnsupportedOperationException.class, () -> IMPL.createLinkBuilder());
    }
}
