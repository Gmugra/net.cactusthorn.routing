package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.FormPart;

public class FormPartParameter extends MethodParameter {

    protected static final String WRONG_TYPE = "@FormPart can be used only for javax.servlet.http.Part type; Method: %s";

    public FormPartParameter(Method method, Parameter parameter) {
        super(parameter);
        if (classType() != Part.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method);
        }
    }

    @Override //
    protected String findName(Parameter parameter) {
        String name = parameter.getAnnotation(FormPart.class).value();
        if ("".equals(name)) {
            return super.findName(parameter);
        }
        return name;
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
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
