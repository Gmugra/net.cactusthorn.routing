package net.cactusthorn.routing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.bodyreader.BodyReader;
import net.cactusthorn.routing.bodyreader.WildcardMessageBodyReader;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.producer.TextPlainProducer;
import net.cactusthorn.routing.validate.ParametersValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public final class RoutingConfig {

    public enum ConfigProperty {

        // @formatter:off
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

    private List<Class<?>> entryPointClasses;

    private ConvertersHolder convertersHolder;

    private Map<String, Producer> producers;

    private List<BodyReader> bodyReaders;

    private ComponentProvider componentProvider;

    private Map<ConfigProperty, Object> configProperties;

    private ParametersValidator validator;

    private String applicationPath;

    // @formatter:off
    private RoutingConfig(
                ComponentProvider componentProvider,
                ConvertersHolder convertersHolder,
                List<Class<?>> entryPointClasses,
                Map<String, Producer> producers,
                List<BodyReader> bodyReaders,
                Map<ConfigProperty, Object> configProperties,
                ParametersValidator validator,
                String applicationPath) {
        this.componentProvider = componentProvider;
        this.convertersHolder = convertersHolder;
        this.entryPointClasses = entryPointClasses;
        this.producers = producers;
        this.bodyReaders = bodyReaders;
        this.configProperties = configProperties;
        this.validator = validator;
        this.applicationPath = applicationPath;
    }
    // @formatter:off

    public static Builder builder(ComponentProvider componentProvider) {
        return new Builder(componentProvider);
    }

    public ConvertersHolder convertersHolder() {
        return convertersHolder;
    }

    public List<Class<?>> entryPointClasses() {
        return entryPointClasses;
    }

    public Map<String, Producer> producers() {
        return producers;
    }

    public List<BodyReader> bodyReaders() {
        return bodyReaders;
    }

    public ComponentProvider provider() {
        return componentProvider;
    }

    public Map<ConfigProperty, Object> properties() {
        return configProperties;
    }

    public Optional<ParametersValidator> validator() {
        return Optional.ofNullable(validator);
    }

    public String applicationPath() {
        return applicationPath;
    }

    public static final class Builder {

        private ComponentProvider componentProvider;

        private final ConvertersHolder convertersHolder = new ConvertersHolder();

        private final List<Class<?>> entryPointClasses = new ArrayList<>();

        private final Map<String, Producer> producers = new HashMap<>();

        private final List<BodyReader> bodyReaders = new ArrayList<>();

        private final Map<ConfigProperty, Object> configProperties = new HashMap<>();

        private ParametersValidator validator;

        private String applicationPath = "/";

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException("ComponentProvider can not be null");
            }
            this.componentProvider = componentProvider;

            for (ConfigProperty property : ConfigProperty.values()) {
                configProperties.put(property, property.ddefault());
            }

            addBodyReader(MediaType.WILDCARD_TYPE, new WildcardMessageBodyReader());

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

        public Builder addBodyReader(MediaType mediaType, MessageBodyReader<?> bodyReader) {
            bodyReaders.add(new BodyReader(mediaType, bodyReader));
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

        public Builder setParametersValidator(ParametersValidator parametersValidator) {
            validator = parametersValidator;
            return this;
        }

        public Builder setApplicationPath(String path) {
            if (path == null) {
                throw new IllegalArgumentException("application-path can not be null");
            }
            applicationPath = path;
            if (applicationPath.charAt(0) != '/') {
                applicationPath = '/' + applicationPath;
            }
            if (!"/".equals(applicationPath) && applicationPath.charAt(applicationPath.length() - 1) != '/') {
                applicationPath += '/';
            }
            return this;
        }

        public RoutingConfig build() {

            Map<String, Producer> unmodifiableProducers = Collections.unmodifiableMap(producers);
            Collections.sort(bodyReaders, BodyReader.COMPARATOR);
            List<BodyReader> unmodifiableBodyReaders = Collections.unmodifiableList(bodyReaders);
            Map<ConfigProperty, Object> unmodifiableConfigProperties = Collections.unmodifiableMap(configProperties);

            return new RoutingConfig(componentProvider, convertersHolder, Collections.unmodifiableList(entryPointClasses),
                    unmodifiableProducers, unmodifiableBodyReaders, unmodifiableConfigProperties, validator, applicationPath);
        }
    }
}
