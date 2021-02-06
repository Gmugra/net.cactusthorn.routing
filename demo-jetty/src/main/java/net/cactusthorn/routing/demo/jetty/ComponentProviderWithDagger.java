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

    private final Map<Class<?>, Function<HttpServletRequest, Resource>> resources = new HashMap<>();

    public ComponentProviderWithDagger(Main main) {
        this.main = main;
        main.resources().entrySet().forEach(e -> resources.put(e.getKey(), new RequestScopeProvider(e.getValue())));
        main.sessionBuilder().build().resources().entrySet()
                .forEach(e -> resources.put(e.getKey(), new SessionScopeProvider(e.getKey())));
    }

    @Override //
    public Object provide(Class<?> clazz, HttpServletRequest request) {
        return resources.get(clazz).apply(request);
    }

    private class RequestScopeProvider implements Function<HttpServletRequest, Resource> {

        private Provider<Resource> resource;

        private RequestScopeProvider(Provider<Resource> entryPoint) {
            this.resource = entryPoint;
        }

        @Override //
        public Resource apply(HttpServletRequest request) {
            return resource.get();
        }
    }

    private class SessionScopeProvider implements Function<HttpServletRequest, Resource> {

        private Class<?> clazz;

        private SessionScopeProvider(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override //
        public Resource apply(HttpServletRequest request) {
            Session component;
            HttpSession session = request.getSession(false);
            if (session == null) {
                session = request.getSession(true);
                component = main.sessionBuilder().build();
                session.setAttribute(Session.class.getName(), component);
            } else {
                component = (Session) session.getAttribute(Session.class.getName());
            }
            return component.resources().get(clazz).get();
        }
    }
}
