package net.cactusthorn.routing.invoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public abstract class MethodParameter {

    private Method method;
    private Parameter parameter;
    private Type genericType;
    private int position;

    public MethodParameter(Method method, Parameter parameter, Type genericType, int position) {
        this.method = method;
        this.parameter = parameter;
        this.genericType = genericType;
        this.position = position;
    }

    protected abstract Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception;

    protected Method method() {
        return method;
    }

    protected Type genericType() {
        return genericType;
    }

    protected Class<?> type() {
        return parameter.getType();
    }

    protected Annotation[] annotations() {
        return parameter.getAnnotations();
    }

    protected <T extends Annotation> T annotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    protected String name() {
        return parameter.getName();
    }

    protected int position() {
        return position;
    }
}
