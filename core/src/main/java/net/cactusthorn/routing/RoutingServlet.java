package net.cactusthorn.routing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.validate.ParametersValidationException;

public class RoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logger.getLogger(RoutingServlet.class.getName());

    private transient Map<String, List<EntryPoint>> allEntryPoints;
    private transient ServletContext servletContext;
    private transient RoutingConfig routingConfig;
    private transient Map<String, Producer> producers;
    private transient String responseCharacterEncoding;
    private transient String defaultRequestCharacterEncoding;

    public RoutingServlet(RoutingConfig config) {
        super();
        routingConfig = config;
        producers = config.producers();
        responseCharacterEncoding = (String) routingConfig.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        defaultRequestCharacterEncoding = (String) routingConfig.properties().get(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING);

        EntryPointScanner scanner = new EntryPointScanner(routingConfig);
        allEntryPoints = scanner.scan();
    }

    @Override //
    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        routingConfig.provider().init(servletContext);
        producers.values().forEach(p -> p.init(servletContext, routingConfig.provider()));
        routingConfig.validator().ifPresent(v -> v.init(servletContext, routingConfig.provider()));
    }

    @Override //
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.HEAD));
    }

    @Override //
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.POST));
    }

    @Override //
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.PUT));
    }

    @Override //
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.DELETE));
    }

    @Override //
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.OPTIONS));
    }

    @Override //
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.GET));
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
        boolean matchContentTypeFail = false;
        for (EntryPoint entryPoint : entryPoints) {
            PathValues values = entryPoint.parse(path);
            if (values != null) {
                if (!entryPoint.matchUserRole(req)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
                if (!entryPoint.matchContentType(contentType)) {
                    matchContentTypeFail = true;
                    continue;
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
        if (matchContentTypeFail) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
        }
    }

    private String contentType(HttpServletRequest req) {
        String consumes = req.getContentType();
        if (consumes == null || consumes.trim().isEmpty()) {
            return MediaType.WILDCARD;
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
