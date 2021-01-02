package net.cactusthorn.routing;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.validate.ParametersValidationException;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logger.getLogger(RoutingServlet.class.getName());

    private transient Map<Class<? extends Annotation>, List<EntryPoint>> allEntryPoints;
    private transient ServletContext servletContext;
    private transient Map<String, Producer> producers;
    private transient Map<String, Consumer> consumers;
    private transient ComponentProvider componentProvider;
    private transient String responseCharacterEncoding;
    private transient String defaultRequestCharacterEncoding;
    private transient Optional<ParametersValidator> parametersValidator;

    public RoutingServlet(RoutingConfig config) {
        super();
        componentProvider = config.provider();
        allEntryPoints = config.entryPoints();
        producers = config.producers();
        consumers = config.consumers();
        responseCharacterEncoding = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        defaultRequestCharacterEncoding = (String) config.properties().get(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING);
        parametersValidator = config.validator();
    }

    @Override //
    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        componentProvider.init(servletContext);
        producers.values().forEach(p -> p.init(servletContext, componentProvider));
        consumers.values().forEach(p -> p.init(servletContext, componentProvider));
        parametersValidator.ifPresent(v -> v.init(servletContext, componentProvider));
    }

    @Override //
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HEAD.class));
    }

    @Override //
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(POST.class));
    }

    @Override //
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(PUT.class));
    }

    @Override //
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(DELETE.class));
    }

    @Override //
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(OPTIONS.class));
    }

    @Override //
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(TRACE.class));
    }

    @Override //
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(GET.class));
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, List<EntryPoint> entryPoints) throws IOException {
        String contentType = contentType(req);
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(defaultRequestCharacterEncoding);
        }
        String path = getPath(contentType, req);
        if (entryPoints.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        }
        for (EntryPoint entryPoint : entryPoints) {
            if (!entryPoint.matchContentType(contentType)) {
                continue;
            }
            PathValues values = entryPoint.parse(path);
            if (values != null) {
                if (!entryPoint.matchUserRole(req)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
                try {
                    Object result = entryPoint.invoke(req, resp, servletContext, values);
                    produce(req, resp, entryPoint, result);
                } catch (ConverterException ce) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ce.getMessage());
                } catch (ParametersValidationException ve) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
                }
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
    }

    private String contentType(HttpServletRequest req) {
        String consumes = req.getContentType();
        if (consumes == null || consumes.trim().isEmpty()) {
            return EntryPointScanner.CONSUMES_DEFAULT;
        }
        return consumes;
    }

    private String getPath(String contentType, HttpServletRequest req) {
        String original = req.getPathInfo();
        String path = original;
        if (path == null || path.isEmpty()) {
            path = "/";
        } else if (!"/".equals(path) && path.charAt(path.length() - 1) != '/') {
            path += '/';
        }
        LOG.log(Level.FINE, "{0}({1} {2}) PathInfo -> original: \"{3}\", corrected: \"{4}\"",
                new Object[] {req.getMethod(), contentType, req.getCharacterEncoding(), original, path});
        return path;
    }

    private void produce(HttpServletRequest req, HttpServletResponse resp, EntryPoint entryPoint, Object result) throws IOException {
        if (result != null && result.getClass() == Response.class) {
            produce(req, resp, entryPoint, (Response) result);
            return;
        }
        resp.setCharacterEncoding(responseCharacterEncoding);
        String contentType = entryPoint.produces();
        resp.setContentType(contentType);
        producers.get(contentType).produce(result, entryPoint.template(), contentType, req, resp);
        LOG.log(Level.FINE, "Producer processing done for Content-Type: {0}", new Object[] {contentType});
    }

    private void produce(HttpServletRequest req, HttpServletResponse resp, EntryPoint entryPoint, Response response) throws IOException {

        response.cookies().forEach(c -> resp.addCookie(c));

        response.headers().entrySet().forEach(e -> e.getValue().forEach(v -> resp.addHeader(e.getKey(), v)));
        response.intHeaders().entrySet().forEach(e -> e.getValue().forEach(v -> resp.addIntHeader(e.getKey(), v)));
        response.dateHeaders().entrySet().forEach(e -> e.getValue().forEach(v -> resp.addDateHeader(e.getKey(), v)));

        if (response.redirect().isPresent()) {
            response.redirect().get().apply(resp);
            return;
        }

        resp.setCharacterEncoding(response.characterEncoding().orElse(responseCharacterEncoding));

        String contentType = response.contentType().orElse(entryPoint.produces());
        resp.setContentType(contentType);

        if (response.statusCode() != HttpServletResponse.SC_OK) {
            resp.setStatus(response.statusCode());
        }

        String template = response.template().orElse(entryPoint.template());

        if (response.skipProducer()) {
            if (response.body() != null) {
                resp.getWriter().write(String.valueOf(response.body()));
            }
            LOG.fine("Producer processing skipped!");
            return;
        }
        producers.get(contentType).produce(response.body(), template, contentType, req, resp);
        LOG.log(Level.FINE, "Producer processing done for Content-Type: {0}", new Object[] {contentType});
    }
}
