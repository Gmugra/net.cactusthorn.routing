package net.cactusthorn.routing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Templated {

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private String template;
    private Object entity;

    public Templated(HttpServletRequest req, HttpServletResponse resp, String template, Object entity) {
        this.req = req;
        this.resp = resp;
        this.template = template;
        this.entity = entity;
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
