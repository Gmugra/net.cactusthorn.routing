package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RuntimeDelegateImplTest {

    private final static RuntimeDelegateImpl IMPL = new RuntimeDelegateImpl();

    @Test //
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> IMPL.createHeaderDelegate(null));
    }
}
