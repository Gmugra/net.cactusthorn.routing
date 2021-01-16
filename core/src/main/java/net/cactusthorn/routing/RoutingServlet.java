package net.cactusthorn.routing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.producer.Producer;

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
    }

    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        routingConfig.provider().init(servletContext);
        producers.values().forEach(p -> p.init(servletContext, routingConfig.provider()));
        routingConfig.bodyReaders().forEach(r -> r.init(servletContext, routingConfig));
        routingConfig.validator().ifPresent(v -> v.init(servletContext, routingConfig.provider()));

        EntryPointScanner scanner = new EntryPointScanner(routingConfig);
        allEntryPoints = scanner.scan();
    }

    @Override //
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase(HttpMethod.PATCH)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEntryPoints.get(HttpMethod.PATCH));
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
                try {
                    if (!entryPoint.matchContentType(contentType)) {
                        matchContentTypeFail = true;
                        continue;
                    }
                    javax.ws.rs.core.Response result = entryPoint.invoke(req, resp, servletContext, values);
                    produce(req, resp, entryPoint, result);
                } catch (WebApplicationException wae) {
                    resp.sendError(wae.getResponse().getStatus(), wae.getMessage());
                } catch (Exception e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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

    private void produce(HttpServletRequest req, HttpServletResponse resp, EntryPoint entryPoint, javax.ws.rs.core.Response result)
            throws IOException {
        StatusType status = result.getStatusInfo();
        resp.setStatus(status.getStatusCode());
        if (status.getStatusCode() == Status.NO_CONTENT.getStatusCode()) {
            writeHeaders(resp, result);
            return;
        }
        if (status.getFamily() == Status.Family.REDIRECTION) {
            writeHeaders(resp, result);
            return;
        }

        String contentType = entryPoint.produces();
        MediaType mediaType = result.getMediaType();
        if (mediaType != null) {
            contentType = mediaType.toString();
        }
        resp.setContentType(contentType);
        resp.setCharacterEncoding(responseCharacterEncoding);

        String template = null;
        Object entity = result.getEntity();
        if (entity instanceof Templated) {
            Templated templated = (Templated) entity;
            template = templated.template();
            entity = templated.entity();
        }
        producers.get(contentType).produce(entity, template, contentType, req, resp);
        LOG.log(Level.FINE, "Producer processing done for Content-Type: {0}", new Object[] {contentType});
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) //
    private void writeHeaders(HttpServletResponse response, javax.ws.rs.core.Response result) {
        for (Map.Entry<String, List<Object>> entry : result.getHeaders().entrySet()) {
            String name = entry.getKey();
            for (Object header : entry.getValue()) {
                if (header == null) {
                    continue;
                }
                HeaderDelegate headerDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(header.getClass());
                if (headerDelegate != null) {
                    response.addHeader(name, headerDelegate.toString(header));
                } else {
                    response.addHeader(name, header.toString());
                }
            }
        }
    }
}
