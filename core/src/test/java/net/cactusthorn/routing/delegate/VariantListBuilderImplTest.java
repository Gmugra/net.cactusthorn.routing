package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Variant.VariantListBuilder;

import org.junit.jupiter.api.Test;

public class VariantListBuilderImplTest {

    @Test public void addEmpty() {
        VariantListBuilder builder = new VariantListBuilderImpl();
        assertThrows(UnsupportedOperationException.class, () -> builder.add());
    }

    @Test public void single() {
        List<Variant> variants = new VariantListBuilderImpl().mediaTypes(MediaType.TEXT_HTML_TYPE).build();
        assertEquals(1, variants.size());

        variants = new VariantListBuilderImpl().languages(new Locale("en", "GB")).build();
        assertEquals(1, variants.size());

        variants = new VariantListBuilderImpl().encodings("UTF-8").build();
        assertEquals(1, variants.size());
    }

    @Test public void testAdd() {
        VariantListBuilder builder = new VariantListBuilderImpl();

        builder.mediaTypes(MediaType.TEXT_HTML_TYPE).add();
        builder.mediaTypes(MediaType.APPLICATION_JSON_TYPE).add();
        List<Variant> variants = builder.build();

        assertEquals(2, variants.size());
    }

    @Test public void usual() {
        VariantListBuilder builder = new VariantListBuilderImpl();
        builder.mediaTypes(MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_JSON_TYPE);
        builder.languages(new Locale("en", "GB"), new Locale("de", "DE"));
        builder.encodings("UTF-8", "KOI8-R");

        List<Variant> variants = builder.build();
        assertEquals(8, variants.size());
    }

    @Test public void empty() {
        VariantListBuilder builder = new VariantListBuilderImpl();
        List<Variant> variants = builder.build();
        assertEquals(0, variants.size());
    }

    @Test public void setNull() {
        VariantListBuilder builder = new VariantListBuilderImpl();
        builder.mediaTypes(MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_JSON_TYPE);
        builder.languages(new Locale("en", "GB"), new Locale("de", "DE"));
        builder.encodings("UTF-8", "KOI8-R");
        builder.mediaTypes((MediaType[]) null);
        builder.languages((Locale[]) null);
        builder.encodings((String[]) null);
        List<Variant> variants = builder.build();
        assertEquals(8, variants.size());
    }

}
