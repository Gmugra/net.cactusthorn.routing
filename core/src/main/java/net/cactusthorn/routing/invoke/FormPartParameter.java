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
import net.cactusthorn.routing.convert.ConverterException;

public class FormPartParameter extends MethodParameter {

    protected static final String WRONG_TYPE = "@FormPart can be used only for javax.servlet.http.Part type; Method: %s";

    private String name;

    public FormPartParameter(Method method, Parameter parameter) {
        super(parameter);
        name = parameter.getAnnotation(FormPart.class).value();
        if (classType() != Part.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method);
        }
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {
            Collection<Part> parts = req.getParts();
            if (parts == null) {
                return null;
            }
            for (Part part : req.getParts()) {
                if (name.equals(part.getName())) {
                    return part;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ConverterException("Type Converting problem", e);
        }
    }
}
