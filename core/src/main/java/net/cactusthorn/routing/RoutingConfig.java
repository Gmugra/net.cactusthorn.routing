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

    public enum ConfigProperty {

        READ_BODY_BUFFER_SIZE(new Integer(1024)),
        RESPONSE_CHARACTER_ENCODING("UTF-8");

        private final Object $default;

        ConfigProperty(Object $default) {
            this.$default = $default;
        }

        public Object $default() {
            return $default;
        }
    }

    private Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints;

    private Map<String, Producer> producers;

    private ComponentProvider componentProvider;

    private Map<ConfigProperty, Object> configProperties;

    private RoutingConfig(ComponentProvider componentProvider, Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints,
            Map<String, Producer> producers, Map<ConfigProperty, Object> configProperties) {
        this.componentProvider = componentProvider;
        this.entryPoints = entryPoints;
        this.producers = producers;
        this.configProperties = configProperties;
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

    public ComponentProvider provider() {
        return componentProvider;
    }

    public Map<ConfigProperty, Object> properties() {
        return configProperties;
    }

    public static class Builder {

        private ComponentProvider componentProvider;

        private final ConvertersHolder convertersHolder = new ConvertersHolder();

        private final List<Class<?>> entryPointClasses = new ArrayList<>();

        private final Map<String, Producer> producers = new HashMap<>();

        private final Map<ConfigProperty, Object> configProperties = new HashMap<>();

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException("ComponentProvider can not be null");
            }
            this.componentProvider = componentProvider;

            for (ConfigProperty property : ConfigProperty.values()) {
                configProperties.put(property, property.$default());
            }
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
            convertersHolder.register(mediaType, consumer);
            return this;
        }

        public Builder setReadBodyBufferSize(int size) {
            configProperties.put(ConfigProperty.READ_BODY_BUFFER_SIZE, size);
            return this;
        }
        
        public Builder setResponseCharacterEncoding(String encoding) {
            configProperties.put(ConfigProperty.RESPONSE_CHARACTER_ENCODING, encoding);
            return this;
        }

        public RoutingConfig build() {

            Map<String, Producer> unmodifiableProducers = Collections.unmodifiableMap(producers);
            Map<ConfigProperty, Object> unmodifiableConfigProperties = Collections.unmodifiableMap(configProperties);

            EntryPointScanner scanner = new EntryPointScanner(entryPointClasses, componentProvider, convertersHolder,
                    unmodifiableConfigProperties);
            Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();

            return new RoutingConfig(componentProvider, Collections.unmodifiableMap(entryPoints), unmodifiableProducers,
                    unmodifiableConfigProperties);
        }
    }
}
