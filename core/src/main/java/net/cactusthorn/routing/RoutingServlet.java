package net.cactusthorn.routing;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConverterException;

public class RoutingServlet extends HttpServlet {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = LoggerFactory.getLogger(RoutingServlet.class);

    private transient Map<Class<? extends Annotation>, List<EntryPoint>> allEentryPoints;
    private transient ServletContext servletContext;
    private transient Map<String, Producer> producers;
    private transient Map<String, Consumer> consumers;
    private transient ComponentProvider componentProvider;
    private transient String responseCharacterEncoding;

    public RoutingServlet(RoutingConfig config) {
        super();
        componentProvider = config.provider();
        allEentryPoints = config.entryPoints();
        producers = config.producers();
        consumers = config.consumers();
        responseCharacterEncoding = (String) config.properties().get(ConfigProperty.RESPONSE_CHARACTER_ENCODING);
    }

    @Override //
    public void init() throws ServletException {
        super.init();
        servletContext = getServletContext();
        componentProvider.init(servletContext);
        producers.values().forEach(p -> p.init(servletContext, componentProvider));
        consumers.values().forEach(p -> p.init(servletContext, componentProvider));
    }

    @Override //
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(HEAD.class));
    }

    @Override //
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(POST.class));
    }

    @Override //
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(PUT.class));
    }

    @Override //
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(DELETE.class));
    }

    @Override //
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(OPTIONS.class));
    }

    @Override //
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(TRACE.class));
    }

    @Override //
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, allEentryPoints.get(GET.class));
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, List<EntryPoint> entryPoints) throws IOException {
        if (entryPoints.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        }
        String path = getPath(req);
        String contentType = contentType(req);
        for (EntryPoint entryPoint : entryPoints) {
            if (!entryPoint.matchContentType(contentType)) {
                continue;
            }
            PathValues values = entryPoint.parse(path);
            if (values != null) {
                try {
                    Object result = entryPoint.invoke(req, resp, servletContext, values);
                    produce(req, resp, entryPoint, result);
                } catch (ConverterException ce) {
                    // the inability to convert some parameter(s) is interpreted as a path not found
                    LOG.trace("", ce);
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
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

    private String getPath(HttpServletRequest req) {
        String original = req.getPathInfo();
        String path = original;
        if (path == null || path.isEmpty()) {
            path = "/";
        } else if (!"/".equals(path) && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        LOG.debug("{} PathInfo -> original: \"{}\", corrected: \"{}\"", req.getMethod(), original, path);
        return path;
    }

    private void produce(HttpServletRequest req, HttpServletResponse resp, EntryPoint entryPoint, Object result) throws IOException {
        resp.setCharacterEncoding(responseCharacterEncoding);
        if (producers.containsKey(entryPoint.produces())) {
            resp.setContentType(entryPoint.produces());
            producers.get(entryPoint.produces()).produce(result, entryPoint.producerTemplate(), entryPoint.produces(), req, resp);
        } else {
            resp.setContentType(EntryPointScanner.PRODUCES_DEFAULT);
            if (result != null) {
                resp.getWriter().write(String.valueOf(result));
            }
        }
    }
}
