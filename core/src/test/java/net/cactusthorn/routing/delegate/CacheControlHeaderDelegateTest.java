package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import javax.ws.rs.core.CacheControl;

import org.junit.jupiter.api.Test;

public class CacheControlHeaderDelegateTest {

    private static final CacheControlHeaderDelegate DELEGATE = new CacheControlHeaderDelegate();

    @Test public void toStringPublic() {
        CacheControl cacheControl = new CacheControl();
        Map<String, String> extension = cacheControl.getCacheExtension();
        extension.put("aaaa", "bb bb");
        extension.put("cccc", "dddd");
        assertEquals("public, no-transform, aaaa=\"bb bb\", cccc=dddd", DELEGATE.toString(cacheControl));
    }

    @Test public void toStringPrivate() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setPrivate(true);
        cacheControl.setNoTransform(false);
        cacheControl.setMaxAge(10);
        cacheControl.setSMaxAge(20);
        cacheControl.getPrivateFields().add("xyz");
        assertEquals("private=\"xyz\", max-age=10, s-maxage=20", DELEGATE.toString(cacheControl));
    }

    @Test public void _toStringNoCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setProxyRevalidate(true);
        cacheControl.setNoStore(true);
        cacheControl.getNoCacheFields().add("xyz");
        assertEquals("public, no-cache=\"xyz\", no-store, no-transform, must-revalidate, proxy-revalidate",
                DELEGATE.toString(cacheControl));
    }

    @Test public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }

    @Test public void fromString() {
        assertThrows(UnsupportedOperationException.class, () -> DELEGATE.fromString(null));
    }
}
