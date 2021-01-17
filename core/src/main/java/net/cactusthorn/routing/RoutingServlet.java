package net.cactusthorn.routing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.body.writer.BodyWriter;
import net.cactusthorn.routing.body.writer.MessageBodyHeadersWriter;
import net.cactusthorn.routing.invoke.MethodInvoker.ReturnObjectInfo;
import net.cactusthorn.routing.PathTemplate.PathValues;

public class RoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logger.getLogger(RoutingServlet.class.getName());

    private transient Map<String, List<EntryPoint>> allEntryPoints;
    private transient ServletContext servletContext;
    private transient RoutingConfig routingConfig;
    private transient String responseCharacterEncoding;
    private transient String defaultRequestCharacterEncoding;

    public RoutingServlet(RoutingConfig config) {
        super();
        routingConfig = config;
        responseCharacterEncoding = (String) routingConfig.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
        defaultRequestCharacterEncoding = (String) routingConfig.properties().get(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING);
    }

    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        routingConfig.provider().init(servletContext);
        routingConfig.bodyWriters().forEach(w -> w.init(servletContext, routingConfig));
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
        List<MediaType> accept = Http.parseAccept(req);
        boolean matchContentTypeFail = false;
        boolean matchAcceptFail = false;
        for (EntryPoint entryPoint : entryPoints) {
            PathValues pathValues = entryPoint.parse(path);
            if (pathValues != null) {
                if (!entryPoint.matchUserRole(req)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                    return;
                }
                try {
                    if (!entryPoint.matchContentType(contentType)) {
                        matchContentTypeFail = true;
                        continue;
                    }
                    if (!entryPoint.matchAccept(accept)) {
                        matchAcceptFail = true;
                        continue;
                    }
                    Response result = entryPoint.invoke(req, resp, servletContext, pathValues, accept);
                    produce(resp, entryPoint, result);
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
        } else if (matchAcceptFail) {
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Not acceptable");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
        }
    }

    private String contentType(HttpServletRequest req) {
        String contentType = req.getContentType();
        if (contentType == null || contentType.trim().isEmpty()) {
            return MediaType.WILDCARD;
        }
        return contentType;
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

    @SuppressWarnings({ "unchecked", "rawtypes" }) //
    private void produce(HttpServletResponse resp, EntryPoint entryPoint, Response result) throws IOException {

        StatusType status = result.getStatusInfo();
        resp.setStatus(status.getStatusCode());
        if (status.getStatusCode() == Status.NO_CONTENT.getStatusCode() || status.getFamily() == Status.Family.REDIRECTION) {
            Http.writeHeaders(resp, result.getHeaders());
            return;
        }

        MediaType responseMediaType = Http.findResponseMediaType(result, entryPoint.produces(), responseCharacterEncoding);
        resp.setContentType(responseMediaType.toString()); // it also set CharacterEncodings

        ReturnObjectInfo info = entryPoint.returnObjectInfo();

        MessageBodyHeadersWriter writer = new MessageBodyHeadersWriter(resp, findBodyWriter(responseMediaType));

        writer.writeTo(result.getEntity(), info.type(), info.genericType(), info.annotations(), responseMediaType, result.getHeaders(),
                resp.getOutputStream());

        LOG.log(Level.FINE, "Producer processing done for Content-Type: {0}", new Object[] {responseMediaType});
    }

    @SuppressWarnings("rawtypes") //
    private MessageBodyWriter findBodyWriter(MediaType responseMediaType) {
        for (BodyWriter bodyWriter : routingConfig.bodyWriters()) {
            if (responseMediaType.isCompatible(bodyWriter.mediaType())) {
                return bodyWriter.messageBodyWriter();
            }
        }
        throw new ServerErrorException("MessageBodyWriter not found", Status.INTERNAL_SERVER_ERROR);
    }
}
