package net.cactusthorn.routing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import net.cactusthorn.routing.body.reader.BodyReader;
import net.cactusthorn.routing.body.reader.InputStreamMessageBodyReader;
import net.cactusthorn.routing.body.reader.StringMessageBodyReader;
import net.cactusthorn.routing.body.reader.ConvertersMessageBodyReader;
import net.cactusthorn.routing.body.writer.BodyWriter;
import net.cactusthorn.routing.body.writer.ObjectMessageBodyWriter;
import net.cactusthorn.routing.body.writer.StringMessageBodyWriter;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.util.ExceptionMapperWrapper;
import net.cactusthorn.routing.util.Messages;
import net.cactusthorn.routing.util.ProvidersImpl;
import net.cactusthorn.routing.validate.ParametersValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;

public final class RoutingConfig {

    public enum ConfigProperty {

        // @formatter:off
        RESPONSE_CHARACTER_ENCODING("UTF-8"),
        DEFAULT_REQUEST_CHARACTER_ENCODING("UTF-8"),
        IO_BUFFER_SIZE(1024);
        // @formatter:on

        private final Object ddefault;

        ConfigProperty(Object ddefault) {
            this.ddefault = ddefault;
        }

        public Object ddefault() {
            return ddefault;
        }
    }

    private final List<Class<?>> resourceClasses;

    private final ConvertersHolder convertersHolder;

    private final Providers providers;

    private final ComponentProvider componentProvider;

    private final Map<ConfigProperty, Object> configProperties;

    private final Optional<ParametersValidator> validator;

    // @formatter:off
    private RoutingConfig(
                ComponentProvider componentProvider,
                ConvertersHolder convertersHolder,
                List<Class<?>> resourceClasses,
                Providers providers,
                Map<ConfigProperty, Object> configProperties,
                Optional<ParametersValidator> validator) {
        this.componentProvider = componentProvider;
        this.convertersHolder = convertersHolder;
        this.resourceClasses = resourceClasses;
        this.providers = providers;
        this.configProperties = configProperties;
        this.validator = validator;
    }
    // @formatter:off

    public static Builder builder(ComponentProvider componentProvider) {
        return new Builder(componentProvider);
    }

    public ConvertersHolder convertersHolder() {
        return convertersHolder;
    }

    public List<Class<?>> resourceClasses() {
        return resourceClasses;
    }

    public Providers providers() {
        return providers;
    }

    public ComponentProvider provider() {
        return componentProvider;
    }

    public Map<ConfigProperty, Object> properties() {
        return configProperties;
    }

    public Optional<ParametersValidator> validator() {
        return validator;
    }

    public static final class Builder {

        private ComponentProvider componentProvider;

        private final List<ParamConverterProvider> converterProviders = new ArrayList<>();

        private final List<Class<?>> resourceClasses = new ArrayList<>();

        private final List<BodyWriter> bodyWriters = new ArrayList<>();

        private final List<BodyReader> bodyReaders = new ArrayList<>();

        private final List<ExceptionMapperWrapper<? extends Throwable>> exceptionMappers = new ArrayList<>();

        private final Map<ConfigProperty, Object> configProperties = new EnumMap<>(ConfigProperty.class);

        private ParametersValidator validator;

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException(Messages.isNull("componentProvider"));
            }
            this.componentProvider = componentProvider;

            for (ConfigProperty property : ConfigProperty.values()) {
                configProperties.put(property, property.ddefault());
            }

            addBodyReader(new InputStreamMessageBodyReader());
            addBodyReader(new StringMessageBodyReader());
            addBodyReader(new ConvertersMessageBodyReader());

            addBodyWriter(new StringMessageBodyWriter());
            addBodyWriter(new ObjectMessageBodyWriter());
        }

        public Builder addParamConverterProvider(ParamConverterProvider provider) {
            if (provider == null) {
                throw new IllegalArgumentException(Messages.isNull("provider"));
            }
            converterProviders.add(provider);
            return this;
        }

        public Builder addResource(Class<?> resource) {
            if (resource == null) {
                throw new IllegalArgumentException(Messages.isNull("resource"));
            }
            validateResource(resource);
            resourceClasses.add(resource);
            return this;
        }

        public Builder addResource(Collection<Class<?>> resources) {
            if (resources == null) {
                throw new IllegalArgumentException(Messages.isNull("resource"));
            }
            resources.forEach(r -> validateResource(r));
            resourceClasses.addAll(resources);
            return this;
        }

        public Builder addBodyWriter(MessageBodyWriter<?> messageBodyWriter) {
            bodyWriters.add(new BodyWriter(messageBodyWriter));
            return this;
        }

        public Builder addBodyReader(MessageBodyReader<?> messageBodyReader) {
            bodyReaders.add(new BodyReader(messageBodyReader));
            return this;
        }

        public Builder addExceptionMapper(ExceptionMapper<? extends Throwable> exceptionMapper) {
            exceptionMappers.add(new ExceptionMapperWrapper<>(exceptionMapper));
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

        public Builder setIOBufferSize(int bufferSize) {
            configProperties.put(ConfigProperty.IO_BUFFER_SIZE, bufferSize);
            return this;
        }

        public Builder setParametersValidator(ParametersValidator parametersValidator) {
            validator = parametersValidator;
            return this;
        }

        public RoutingConfig build() {
            // @formatter:off
            return new RoutingConfig(
                    componentProvider,
                    new ConvertersHolder(converterProviders),
                    Collections.unmodifiableList(resourceClasses),
                    new ProvidersImpl(bodyReaders, bodyWriters, exceptionMappers),
                    Collections.unmodifiableMap(configProperties),
                    Optional.ofNullable(validator));
            // @formatter:on
        }

        private void validateResource(Class<?> resource) {
            if (resource.getAnnotation(Path.class) != null) {
                return;
            }
            // @formatter:off
            Arrays.stream(resource.getMethods())
                .flatMap(m -> Arrays.stream(m.getAnnotations()))
                .filter(a -> a.annotationType().getAnnotation(HttpMethod.class) != null)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(Messages.msg(Messages.Key.WRONG_ROOT_RESOURCE_CLASS, resource.getName())));
            // @formatter:on
        }
    }
}
