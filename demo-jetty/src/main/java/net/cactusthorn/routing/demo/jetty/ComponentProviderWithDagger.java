package net.cactusthorn.routing.demo.jetty;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Provider;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.demo.jetty.dagger.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class ComponentProviderWithDagger implements ComponentProvider {

    private Main main;

    private final Map<Class<?>, Function<HttpServletRequest, EntryPoint>> entryPoints = new HashMap<>();

    public ComponentProviderWithDagger(Main main) {
        this.main = main;
        main.entryPoints().entrySet().forEach(e -> entryPoints.put(e.getKey(), new RequestScopeProvider(e.getValue())));
        main.sessionBuilder().build().entryPoints().entrySet()
                .forEach(e -> entryPoints.put(e.getKey(), new SessionScopeProvider(e.getKey())));
    }

    @Override //
    public Object provide(Class<?> clazz, HttpServletRequest request) {
        return entryPoints.get(clazz).apply(request);
    }

    private class RequestScopeProvider implements Function<HttpServletRequest, EntryPoint> {

        private Provider<EntryPoint> entryPoint;

        private RequestScopeProvider(Provider<EntryPoint> entryPoint) {
            this.entryPoint = entryPoint;
        }

        @Override //
        public EntryPoint apply(HttpServletRequest request) {
            return entryPoint.get();
        }
    }

    private class SessionScopeProvider implements Function<HttpServletRequest, EntryPoint> {

        private Class<?> clazz;

        private SessionScopeProvider(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override //
        public EntryPoint apply(HttpServletRequest request) {
            Session component;
            HttpSession session = request.getSession(false);
            if (session == null) {
                session = request.getSession(true);
                component = main.sessionBuilder().build();
                session.setAttribute(Session.class.getName(), component);
            } else {
                component = (Session) session.getAttribute(Session.class.getName());
            }
            return component.entryPoints().get(clazz).get();
        }
    }
}
