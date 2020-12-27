package net.cactusthorn.routing;

import java.util.List;
import java.util.Map;

import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.producer.TextPlainProducer;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public final class RoutingConfig {

    public enum ConfigProperty {

        // @formatter:off
        READ_BODY_BUFFER_SIZE(1024),
        RESPONSE_CHARACTER_ENCODING("UTF-8"),
        DEFAULT_REQUEST_CHARACTER_ENCODING("UTF-8");
        // @formatter:on

        private final Object ddefault;

        ConfigProperty(Object ddefault) {
            this.ddefault = ddefault;
        }

        public Object ddefault() {
            return ddefault;
        }
    }

    private Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints;

    private Map<String, Producer> producers;

    private Map<String, Consumer> consumers;

    private ComponentProvider componentProvider;

    private Map<ConfigProperty, Object> configProperties;

    // @formatter:off
    private RoutingConfig(
                ComponentProvider componentProvider,
                Map<Class<? extends Annotation>,
                List<EntryPoint>> entryPoints,
                Map<String, Producer> producers,
                Map<String, Consumer> consumers,
                Map<ConfigProperty, Object> configProperties) {

        this.componentProvider = componentProvider;
        this.entryPoints = entryPoints;
        this.producers = producers;
        this.consumers = consumers;
        this.configProperties = configProperties;
    }
    // @formatter:off

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

    public Map<ConfigProperty, Object> properties() {
        return configProperties;
    }

    public static final class Builder {

        private ComponentProvider componentProvider;

        private final ConvertersHolder convertersHolder = new ConvertersHolder();

        private final List<Class<?>> entryPointClasses = new ArrayList<>();

        private final Map<String, Producer> producers = new HashMap<>();

        private final Map<String, Consumer> consumers = new HashMap<>();

        private final Map<ConfigProperty, Object> configProperties = new HashMap<>();

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException("ComponentProvider can not be null");
            }
            this.componentProvider = componentProvider;

            for (ConfigProperty property : ConfigProperty.values()) {
                configProperties.put(property, property.ddefault());
            }

            addProducer(TextPlainProducer.MEDIA_TYPE, new TextPlainProducer());
        }

        public Builder addConverter(Class<?> clazz, Converter converter) {
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

        public Builder setDefaultRequestCharacterEncoding(String encoding) {
            configProperties.put(ConfigProperty.DEFAULT_REQUEST_CHARACTER_ENCODING, encoding);
            return this;
        }

        public RoutingConfig build() {

            Map<String, Producer> unmodifiableProducers = Collections.unmodifiableMap(producers);
            Map<String, Consumer> unmodifiableConsumers = Collections.unmodifiableMap(consumers);
            Map<ConfigProperty, Object> unmodifiableConfigProperties = Collections.unmodifiableMap(configProperties);

            EntryPointScanner scanner = new EntryPointScanner(entryPointClasses, componentProvider, convertersHolder,
                    unmodifiableConfigProperties);
            Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();

            return new RoutingConfig(componentProvider, Collections.unmodifiableMap(entryPoints), unmodifiableProducers,
                    unmodifiableConsumers, unmodifiableConfigProperties);
        }
    }
}
