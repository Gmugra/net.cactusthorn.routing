package net.cactusthorn.routing.thymeleaf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import org.thymeleaf.context.WebContext;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.Templated;
import net.cactusthorn.routing.body.writer.TemplatedMessageBodyWriter;

@Produces({MediaType.TEXT_HTML})
public class SimpleThymeleafBodyWriter implements TemplatedMessageBodyWriter {

    private ITemplateEngine templateEngine;

    public SimpleThymeleafBodyWriter(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SimpleThymeleafBodyWriter(String prefix) {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        templateEngine = engine;
    }

    private ServletContext servletContext;

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        this.servletContext = servletContext;
    }

    @Override //
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override @SuppressWarnings({ "unchecked", "rawtypes" }) //
    public void writeTo(Templated templated, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        WebContext ctx = new WebContext(templated.request(), templated.response(), servletContext, templated.request().getLocale());

        if (templated.entity() != null) {
            Map<String, Object> variables = new HashMap<>();
            if (templated.entity() instanceof Map) {
                variables.putAll((Map) templated.entity());
            } else {
                variables.put("model", templated.entity());
            }
            ctx.setVariables(variables);
        }

        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        try (Writer writer = new OutputStreamWriter(entityStream, charset)) {
            templateEngine.process(templated.template(), ctx, writer);
        }
    }
}
