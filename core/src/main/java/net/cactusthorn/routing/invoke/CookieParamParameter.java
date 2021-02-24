package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.RuntimeDelegate;

import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.Headers;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.ERROR_AT_PARAMETER_POSITION;

public class CookieParamParameter extends ConvertableMethodParameter {

    private static final Converter<Cookie> COOKIE_CONVERTER = (type, genericType, annotations, value) -> {
        if (value == null) {
            return null;
        }
        return RuntimeDelegate.getInstance().createHeaderDelegate(Cookie.class).fromString(value);
    };

    public CookieParamParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {
        super(method, parameter, genericType, position, convertersHolder);
    }

    @Override //
    public String name() {
        String name = annotation(CookieParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    protected Converter<?> findConverter(ConvertersHolder convertersHolder) {
        if (Cookie.class == converterType()) {
            return COOKIE_CONVERTER;
        }
        return super.findConverter(convertersHolder);
    }

    @Override //
    protected Object createDefaultObject(String defaultValue) {
        String prepared = defaultValue;
        if (defaultValue != null) {
            prepared = name() + '=' + Headers.addQuotesIfContainsWhitespace(defaultValue);
        }
        return super.createDefaultObject(prepared);
    }

    @Override @SuppressWarnings("unchecked") //
    protected <T> T convert(String value) throws Throwable {
        if (value == null) {
            return super.convert((String) null);
        }
        Cookie cookie = COOKIE_CONVERTER.convert(converterType(), converterGenericType(), annotations(), value);
        if (Cookie.class == converterType()) {
            return (T) cookie;
        }
        return super.convert(cookie.getValue());
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            String cookieHeader = req.getHeader(HttpHeaders.COOKIE);
            if (cookieHeader == null) {
                if (collection()) {
                    return convert((String[]) null);
                }
                return convert((String) null);
            }
            List<Cookie> cookies = Headers.parseCookies(name(), cookieHeader);
            if (collection()) {
                return convert(cookies);
            }
            return convert(cookies.isEmpty() ? null : cookies.get(0).toString());
        } catch (Throwable e) {
            throw new BadRequestException(Messages.msg(ERROR_AT_PARAMETER_POSITION, position(), type().getSimpleName(), e), e);
        }
    }

    private <T> Collection<T> convert(List<Cookie> cookies) throws Throwable {
        if (cookies.isEmpty()) {
            return convert((String[]) null);
        }
        String[] arr = new String[cookies.size()];
        for (int i = 0; i < cookies.size(); i++) {
            arr[i] = cookies.get(i).toString();
        }
        return convert(arr);
    }
}
