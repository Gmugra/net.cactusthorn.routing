package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.FormPart;

public class FormPartParameter extends MethodParameter {

    private static final String WRONG_TYPE = "@FormPart can be used only for javax.servlet.http.Part type; Method: %s";

    public FormPartParameter(Method method, Parameter parameter, Type genericType, int position) {
        super(method, parameter, genericType, position);

        //TODO MULTIPART_FORM_DATA_TYPE check

        if (type() != Part.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method());
        }
    }

    @Override //
    public String name() {
        String name = annotation(FormPart.class).value();
        if ("".equals(name)) {
            return super.name();
        }
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
