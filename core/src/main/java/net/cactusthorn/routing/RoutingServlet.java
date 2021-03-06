package net.cactusthorn.routing;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.*;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.body.writer.MessageBodyHeadersWriter;
import net.cactusthorn.routing.invoke.MethodInvoker.ReturnObjectInfo;
import net.cactusthorn.routing.resource.ResourceScanner;
import net.cactusthorn.routing.resource.ResourceScanner.Resource;
import net.cactusthorn.routing.uri.UriComponentEncoder;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.Headers;
import net.cactusthorn.routing.util.Http;

import net.cactusthorn.routing.util.Messages;
import net.cactusthorn.routing.util.ProvidersImpl;

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
        ((ProvidersImpl) routingConfig.providers()).init(servletContext, routingConfig);
        routingConfig.validator().ifPresent(v -> v.init(servletContext, routingConfig.provider()));

        ResourceScanner scanner = new ResourceScanner(routingConfig);
        allResources = scanner.scan();
    }

    @Override //
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod().toUpperCase();
        process(req, resp, allResources.get(method));
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, List<Resource> resources) throws IOException {
        if (resources == null) {
            resp.sendError(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase());
            return;
        }
        String contentType = contentType(req);
        if (req.getCharacterEncoding() == null) {
            req.setCharacterEncoding(defaultRequestCharacterEncoding);
        }
        String path = getPath(contentType, req);
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
                    LOG.info(wae.getMessage());
                    resp.sendError(wae.getResponse().getStatus());
                } catch (Exception e) {
                    LOG.info(e.getMessage());
                    resp.sendError(BAD_REQUEST.getStatusCode());
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
            LOG.fine(Messages.msg(INFO_PATH_INFO, req.getMethod(), contentType, req.getCharacterEncoding(), original, path));
        }
        return UriComponentEncoder.PATH.encode(path);
    }

    private static final Http HTTP = new Http();

    private void produce(HttpServletRequest req, HttpServletResponse resp, Resource resource, Response result, MediaType producesMediaType)
            throws IOException {

        StatusType status = result.getStatusInfo();
        resp.setStatus(status.getStatusCode());
        if (status.getStatusCode() == Status.NO_CONTENT.getStatusCode() || status.getFamily() == Status.Family.REDIRECTION
                || result.getEntity() == null) {
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

        ReturnObjectInfo info = resource.returnObjectInfo(req, resp, result.getEntity());

        MessageBodyHeadersWriter writer = new MessageBodyHeadersWriter(resp, findBodyWriter(responseMediaType, info));

        writer.writeTo(info, responseMediaType, result.getHeaders(), resp.getOutputStream());

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(Messages.msg(INFO_PRODUCER_PROCESSING_DONE, responseMediaType));
        }
    }

    @SuppressWarnings("rawtypes") //
    private MessageBodyWriter findBodyWriter(MediaType responseMediaType, ReturnObjectInfo info) {
        return routingConfig.providers().getMessageBodyWriter(info.type(), info.genericType(), info.annotations(), responseMediaType);
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
