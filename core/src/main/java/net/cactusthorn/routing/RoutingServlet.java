package net.cactusthorn.routing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.*;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.body.writer.BodyWriter;
import net.cactusthorn.routing.body.writer.MessageBodyHeadersWriter;
import net.cactusthorn.routing.body.writer.Templated;
import net.cactusthorn.routing.invoke.MethodInvoker.ReturnObjectInfo;
import net.cactusthorn.routing.resource.ResourceScanner;
import net.cactusthorn.routing.resource.ResourceScanner.Resource;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.Headers;
import net.cactusthorn.routing.util.Http;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.MESSAGE_BODY_WRITER_NOT_FOUND;
import static net.cactusthorn.routing.util.Messages.Key.INFO_PRODUCER_PROCESSING_DONE;
import static net.cactusthorn.routing.util.Messages.Key.INFO_PATH_INFO;

public class RoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logger.getLogger(RoutingServlet.class.getName());

    private transient Map<String, List<Resource>> allResources;
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

        ResourceScanner scanner = new ResourceScanner(routingConfig);
        allResources = scanner.scan();
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
        process(req, resp, allResources.get(HttpMethod.PATCH));
    }

    @Override //
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.HEAD));
    }

    @Override //
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.POST));
    }

    @Override //
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.PUT));
    }

    @Override //
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.DELETE));
    }

    @Override //
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.OPTIONS));
    }

    @Override //
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allResources.get(HttpMethod.GET));
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, List<Resource> resources) throws IOException {
        String contentType = contentType(req);
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(defaultRequestCharacterEncoding);
        }
        String path = getPath(contentType, req);
        if (resources.isEmpty()) {
            resp.sendError(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase());
            return;
        }
        List<MediaType> accept = Headers.parseAccept(req.getHeader(HttpHeaders.ACCEPT));
        boolean matchContentTypeFail = false;
        boolean matchAcceptFail = false;
        for (Resource resource : resources) {
            PathValues pathValues = resource.parse(path);
            if (pathValues != null) {
                if (!resource.matchRolesAllowed(req)) {
                    resp.sendError(FORBIDDEN.getStatusCode(), FORBIDDEN.getReasonPhrase());
                    return;
                }
                try {
                    if (!resource.matchContentType(contentType)) {
                        matchContentTypeFail = true;
                        continue;
                    }
                    Optional<MediaType> producesMediaType = resource.matchAccept(accept);
                    if (!producesMediaType.isPresent()) {
                        matchAcceptFail = true;
                        continue;
                    }
                    Response result = resource.invoke(req, resp, servletContext, pathValues);
                    // It could be that in resource method Response object was created manually and
                    // media-type was set,
                    // and this media-type do not match request Accept-header.
                    // In this case -> response error at ones.
                    if (!matchAccept(accept, result)) {
                        resp.sendError(NOT_ACCEPTABLE.getStatusCode(), NOT_ACCEPTABLE.getReasonPhrase());
                        return;
                    }
                    produce(req, resp, resource, result, producesMediaType.get());
                } catch (WebApplicationException wae) {
                    resp.sendError(wae.getResponse().getStatus(), wae.getMessage());
                } catch (Exception e) {
                    resp.sendError(BAD_REQUEST.getStatusCode(), e.getMessage());
                }
                return;
            }
        }
        if (matchContentTypeFail) {
            resp.sendError(UNSUPPORTED_MEDIA_TYPE.getStatusCode(), UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        } else if (matchAcceptFail) {
            resp.sendError(NOT_ACCEPTABLE.getStatusCode(), NOT_ACCEPTABLE.getReasonPhrase());
        } else {
            resp.sendError(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase());
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
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, Messages.msg(INFO_PATH_INFO, req.getMethod(), contentType, req.getCharacterEncoding(), original, path));
        }
        return path;
    }

    private static final Http HTTP = new Http();

    private void produce(HttpServletRequest req, HttpServletResponse resp, Resource resource, Response result, MediaType producesMediaType)
            throws IOException {

        StatusType status = result.getStatusInfo();
        resp.setStatus(status.getStatusCode());
        if (status.getStatusCode() == Status.NO_CONTENT.getStatusCode() || status.getFamily() == Status.Family.REDIRECTION) {
            HTTP.writeHeaders(resp, result.getHeaders());
            return;
        }

        MediaType responseMediaType = producesMediaType;
        if (result.getMediaType() != null) {
            responseMediaType = result.getMediaType();
        }
        if (responseMediaType.getParameters().get(MediaType.CHARSET_PARAMETER) == null) {
            responseMediaType = responseMediaType.withCharset(responseCharacterEncoding);
        }
        resp.setCharacterEncoding(responseMediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
        resp.setContentType(new MediaType(responseMediaType.getType(), responseMediaType.getSubtype()).toString());

        Object entity = prepareEntity(req, resp, result.getEntity());

        ReturnObjectInfo info = resource.returnObjectInfo().withEntity(entity);

        MessageBodyHeadersWriter writer = new MessageBodyHeadersWriter(resp, findBodyWriter(responseMediaType, info));

        writer.writeTo(entity, info.type(), info.genericType(), info.annotations(), responseMediaType, result.getHeaders(),
                resp.getOutputStream());

        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, Messages.msg(INFO_PRODUCER_PROCESSING_DONE, responseMediaType));
        }
    }

    private Object prepareEntity(HttpServletRequest req, HttpServletResponse resp, Object entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof Templated) {
            Templated templated = (Templated) entity;
            if (templated.request() != null && templated.response() != null) {
                return entity;
            }
            if (templated.request() != null) {
                return new Templated(templated.template(), templated.entity(), templated.request(), resp);
            }
            if (templated.response() != null) {
                return new Templated(templated.template(), templated.entity(), req, templated.response());
            }
            return new Templated(templated.template(), templated.entity(), req, resp);
        }
        return entity;
    }

    @SuppressWarnings("rawtypes") //
    private MessageBodyWriter findBodyWriter(MediaType responseMediaType, ReturnObjectInfo info) {
        for (BodyWriter bodyWriter : routingConfig.bodyWriters()) {
            if (bodyWriter.isProcessable(info.type(), info.genericType(), info.annotations(), responseMediaType)) {
                return bodyWriter.messageBodyWriter();
            }
        }

        // Actually it's impossible, because exists ObjectMessageBodyWriter
        throw new ServerErrorException(Messages.msg(MESSAGE_BODY_WRITER_NOT_FOUND), INTERNAL_SERVER_ERROR);
    }

    private boolean matchAccept(List<MediaType> accept, Response result) {
        if (result.getMediaType() == null) {
            return true;
        }
        for (MediaType acceptMediaType : accept) {
            if (acceptMediaType.isCompatible(result.getMediaType())) {
                return true;
            }
        }
        return false;
    }
}
