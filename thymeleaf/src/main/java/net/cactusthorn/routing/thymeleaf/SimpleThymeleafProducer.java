package net.cactusthorn.routing.thymeleaf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.context.WebContext;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.Producer;

public class SimpleThymeleafProducer implements Producer {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleThymeleafProducer.class);

    private ITemplateEngine templateEngine;

    public SimpleThymeleafProducer(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SimpleThymeleafProducer(String prefix) {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(true);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        templateEngine = engine;
    }

    private ServletContext servletContext;

    @Override //
    public void init(ServletContext servletContext, ComponentProvider componentProvider) {
        this.servletContext = servletContext;
    }

    @Override @SuppressWarnings({ "unchecked", "rawtypes" }) //
    public void produce(Object object, String template, String mediaType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

        if (object != null) {
            Map<String, Object> variables = new HashMap<>();
            if (object instanceof Map) {
                variables.putAll((Map) object);
            } else {
                variables.put("model", object);
            }
            ctx.setVariables(variables);
        }

        templateEngine.process(template, ctx, resp.getWriter());
        LOG.debug("processed template: {}", template);
    }

}
