package net.cactusthorn.routing.body.writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.util.Messages;

public class Templated {

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private String template;
    private Object entity;

    public Templated(String template, Object entity, HttpServletRequest req, HttpServletResponse resp) {
        if (template == null) {
            throw new IllegalArgumentException(Messages.isNull("template"));
        }
        this.template = template;
        this.entity = entity;
        this.req = req;
        this.resp = resp;
    }

    public Templated(String template, Object entity) {
        this(template, entity, null, null);
    }

    public Templated(String template) {
        this(template, null, null, null);
    }

    public HttpServletRequest request() {
        return req;
    }

    public HttpServletResponse response() {
        return resp;
    }

    public String template() {
        return template;
    }

    public Object entity() {
        return entity;
    }
}
