package net.cactusthorn.routing.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

public final class RoutingApplication extends Application {

    private final Set<Class<?>> classes;
    private final Map<String, Object> properties;
    private final Set<Object> singletons;

    private RoutingApplication(Set<Class<?>> classes, Map<String, Object> properties, Set<Object> singletons) {
        this.classes = classes;
        this.properties = properties;
        this.singletons = singletons;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override public Set<Object> getSingletons() {
        return singletons;
    }

    @Override public Map<String, Object> getProperties() {
        return properties;
    }

    public static final class Builder {

        private final Set<Class<?>> classes = new HashSet<>();
        private final Map<String, Object> properties = new HashMap<>();
        private final Set<Object> singletons = new HashSet<>();

        private Builder() {
        }

        public Builder addClass(Class<?> clazz) {
            classes.add(clazz);
            return this;
        }

        public Builder putProperty(String name, Object value) {
            properties.put(name, value);
            return this;
        }

        public Builder addSingleton(Object singleton) {
            singletons.add(singleton);
            return this;
        }

        public RoutingApplication build() {
            // @formatter:off
            return
                new RoutingApplication(
                    Collections.unmodifiableSet(classes),
                    Collections.unmodifiableMap(properties),
                    Collections.unmodifiableSet(singletons));
            // @formatter:on
        }
    }
}
