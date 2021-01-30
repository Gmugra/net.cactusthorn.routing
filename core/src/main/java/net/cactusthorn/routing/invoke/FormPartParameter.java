package net.cactusthorn.routing.invoke;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.FormPart;

public class FormPartParameter implements MethodParameter {

    private static final String WRONG_TYPE = "@FormPart can be used only for javax.servlet.http.Part type; Method: %s";

    private String name;

    public FormPartParameter(ParameterInfo paramInfo) {

        if (paramInfo.type() != Part.class) {
            throw new RoutingInitializationException(WRONG_TYPE, paramInfo.method());
        }

        name = paramInfo.annotation(FormPart.class).value();
        if ("".equals(name)) {
            name = paramInfo.name();
        }
    }

    @Override //
    public String name() {
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        Collection<Part> parts = req.getParts();
        if (parts == null) {
            return null;
        }
        for (Part part : req.getParts()) {
            if (name().equals(part.getName())) {
                return part;
            }
        }
        return null;
    }
}
