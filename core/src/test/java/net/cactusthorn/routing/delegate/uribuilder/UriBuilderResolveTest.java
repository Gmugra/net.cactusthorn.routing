package net.cactusthorn.routing.delegate.uribuilder;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.delegate.UriBuilderImpl;

public class UriBuilderResolveTest {

    @Test public void resolveTemplateSignleNull() {
        UriBuilder builder = new UriBuilderImpl();
        assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplate(null, "aa"));
        assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplate("aa", null));
        assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(null));
        assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(null, true));
        assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplatesFromEncoded(null));
    }

    @Test public void resolveTemplateSingle() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{ var }:a@a.com#ss{var}").resolveTemplate("var", "mailto");
        assertEquals("mailto:a@a.com#ssmailto", builder.build().toString());
    }

    @Test public void resolveTemplateSingle2() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("mailto:a@a.com#ss").scheme("f{ var : \\d{10} }").resolveTemplate("var", "ile");
        assertEquals("file:a@a.com#ss", builder.build().toString());
    }

    @Test public void resolveTemplateFull() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("{ var }://{ var}@{var }/{var}?{var}={var}#{var}").resolveTemplate("var", "file");
        assertEquals("file://file@file/file?file=file#file", builder.build().toString());
    }

    @Test public void resolveTemplateSpash() {
        UriBuilder builder = new UriBuilderImpl();
        builder.replacePath("/{var}").resolveTemplate("var", "aa/b%20b", true);
        assertEquals("/aa%2Fb%2520b", builder.build().toString());
    }

    @Test public void resolveTemplateFromEncoded() {
        UriBuilder builder = new UriBuilderImpl();
        builder.replacePath("/{var}").resolveTemplateFromEncoded("var", "a%20a/bb");
        assertEquals("/a%20a/bb", builder.build().toString());
    }

    @Test public void resolveTemplates() {
        UriBuilder builder = new UriBuilderImpl();
        Map<String, Object> vars = new HashMap<>();
        vars.put("var", "file");
        builder.uri("{ var }://{ var}@{var }/{var}?{var}={var}#{var}").resolveTemplates(vars);
        assertEquals("file://file@file/file?file=file#file", builder.build().toString());
    }

    @Test public void resolveTemplatesSpash() {
        UriBuilder builder = new UriBuilderImpl();
        Map<String, Object> vars = new HashMap<>();
        vars.put("var", "aa/b%20b");
        builder.replacePath("/{var}").resolveTemplates(vars, true);
        assertEquals("/aa%2Fb%2520b", builder.build().toString());
    }

    @Test public void resolveTemplatesFromEncoded() {
        UriBuilder builder = new UriBuilderImpl();
        Map<String, Object> vars = new HashMap<>();
        vars.put("var", "a%20a/bb");
        builder.replacePath("/{var}").resolveTemplatesFromEncoded(vars);
        assertEquals("/a%20a/bb", builder.build().toString());
    }

    @Test public void resolveAuthority() {
        UriBuilder builder = new UriBuilderImpl();
        builder.uri("http://@/{var}").resolveTemplate("var", "path");
        assertEquals("http://@/path", builder.build().toString());
    }

}
