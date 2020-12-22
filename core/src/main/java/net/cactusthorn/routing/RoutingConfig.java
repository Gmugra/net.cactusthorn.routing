package net.cactusthorn.routing;

import java.util.List;
import java.util.Map;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.converter.Converter;
import net.cactusthorn.routing.converter.ConvertersHolder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public final class RoutingConfig {

    private Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints;

    private Map<String, Producer> producers;
    private Map<String, Consumer> consumers;

    private ComponentProvider componentProvider;

    private RoutingConfig(ComponentProvider componentProvider, Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints,
            Map<String, Producer> producers, Map<String, Consumer> consumers) {
        this.componentProvider = componentProvider;
        this.entryPoints = entryPoints;
        this.producers = producers;
        this.consumers = consumers;
    }

    public static Builder builder(ComponentProvider componentProvider) {
        return new Builder(componentProvider);
    }

    public Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints() {
        return entryPoints;
    }

    public Map<String, Producer> producers() {
        return producers;
    }

    public Map<String, Consumer> consumers() {
        return consumers;
    }

    public ComponentProvider provider() {
        return componentProvider;
    }

    public static class Builder {

        private ComponentProvider componentProvider;

        private final ConvertersHolder convertersHolder = new ConvertersHolder();

        private final List<Class<?>> entryPointClasses = new ArrayList<>();

        private final Map<String, Producer> producers = new HashMap<>();

        private final Map<String, Consumer> consumers = new HashMap<>();

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException("ComponentProvider can not be null");
            }
            this.componentProvider = componentProvider;
        }

        public <T> Builder addConverter(Class<T> clazz, Converter<T> converter) {
            convertersHolder.register(clazz, converter);
            return this;
        }

        public Builder addEntryPoint(Class<?> entryPoint) {
            entryPointClasses.add(entryPoint);
            return this;
        }

        public Builder addEntryPoint(Collection<Class<?>> entryPoints) {
            entryPointClasses.addAll(entryPoints);
            return this;
        }

        public Builder addProducer(String mediaType, Producer producer) {
            producers.put(mediaType, producer);
            return this;
        }

        public Builder addConsumer(String mediaType, Consumer consumer) {
            consumers.put(mediaType, consumer);
            return this;
        }

        public RoutingConfig build() {
            EntryPointScanner scanner = new EntryPointScanner(entryPointClasses, componentProvider, convertersHolder);
            Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
            return new RoutingConfig(componentProvider, Collections.unmodifiableMap(entryPoints), Collections.unmodifiableMap(producers),
                    Collections.unmodifiableMap(consumers));
        }
    }
}
