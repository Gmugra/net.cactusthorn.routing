package net.cactusthorn.routing.producer;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextPlainProducer implements Producer {

    public static final String MEDIA_TYPE = "text/plain";

    private static final Logger LOG = Logger.getLogger(TextPlainProducer.class.getName());

    @Override //
    public void produce(Object object, String template, String mediaType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (object == null) {
            if (resp.getStatus() == HttpServletResponse.SC_OK) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            LOG.fine("NO CONTENT");
            return;
        }
        resp.getWriter().write(String.valueOf(object));
    }

}
