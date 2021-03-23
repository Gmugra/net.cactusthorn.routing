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
import javax.ws.rs.core.Application;

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
import net.cactusthorn.routing.util.RoutingApplication;
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

    private final Application application;

    // @formatter:off
    private RoutingConfig(
                ComponentProvider componentProvider,
                ConvertersHolder convertersHolder,
                List<Class<?>> resourceClasses,
                Providers providers,
                Map<ConfigProperty, Object> configProperties,
                Optional<ParametersValidator> validator,
                Application application) {
        this.componentProvider = componentProvider;
        this.convertersHolder = convertersHolder;
        this.resourceClasses = resourceClasses;
        this.providers = providers;
        this.configProperties = configProperties;
        this.validator = validator;
        this.application = application;
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

    public Application application() {
        return application;
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

        private final RoutingApplication.Builder appBuilder = RoutingApplication.builder();

        private Builder(ComponentProvider componentProvider) {
            if (componentProvider == null) {
                throw new IllegalArgumentException(Messages.isNull("componentProvider"));
            }
            this.componentProvider = componentProvider;

            for (ConfigProperty property : ConfigProperty.values()) {
                configProperties.put(property, property.ddefault());
            }

            addMessageBodyReader(new InputStreamMessageBodyReader());
            addMessageBodyReader(new StringMessageBodyReader());
            addMessageBodyReader(new ConvertersMessageBodyReader());

            addMessageBodyWriter(new StringMessageBodyWriter());
            addMessageBodyWriter(new ObjectMessageBodyWriter());
        }

        public Builder addParamConverterProvider(ParamConverterProvider paramConverterProvider) {
            if (paramConverterProvider == null) {
                throw new IllegalArgumentException(Messages.isNull("paramConverterProvider"));
            }
            converterProviders.add(paramConverterProvider);
            appBuilder.addSingleton(paramConverterProvider);
            return this;
        }

        public Builder addParamConverterProvider(Class<?> paramConverterProvider) {
            if (paramConverterProvider == null) {
                throw new IllegalArgumentException(Messages.isNull("paramConverterProvider"));
            }
            try {
                ParamConverterProvider pcp = (ParamConverterProvider) paramConverterProvider.getConstructor().newInstance();
                converterProviders.add(pcp);
                appBuilder.addClass(paramConverterProvider);
                return this;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder addResource(Class<?> resource) {
            if (resource == null) {
                throw new IllegalArgumentException(Messages.isNull("resource"));
            }
            validateResource(resource);
            resourceClasses.add(resource);
            appBuilder.addClass(resource);
            return this;
        }

        public Builder addResource(Collection<Class<?>> resources) {
            if (resources == null) {
                throw new IllegalArgumentException(Messages.isNull("resource"));
            }
            resources.forEach(r -> {
                validateResource(r); appBuilder.addClass(r);
            });
            resourceClasses.addAll(resources);
            return this;
        }

        public Builder addMessageBodyWriter(MessageBodyWriter<?> messageBodyWriter) {
            bodyWriters.add(new BodyWriter(messageBodyWriter));
            appBuilder.addSingleton(messageBodyWriter);
            return this;
        }

        public Builder addMessageBodyWriter(Class<?> messageBodyWriter) {
            try {
                MessageBodyWriter<?> mbw = (MessageBodyWriter<?>) messageBodyWriter.getConstructor().newInstance();
                bodyWriters.add(new BodyWriter(mbw));
                appBuilder.addClass(messageBodyWriter);
                return this;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder addMessageBodyReader(MessageBodyReader<?> messageBodyReader) {
            bodyReaders.add(new BodyReader(messageBodyReader));
            appBuilder.addSingleton(messageBodyReader);
            return this;
        }

        public Builder addMessageBodyReader(Class<?> messageBodyReader) {
            try {
                MessageBodyReader<?> mbr = (MessageBodyReader<?>) messageBodyReader.getConstructor().newInstance();
                bodyReaders.add(new BodyReader(mbr));
                appBuilder.addClass(messageBodyReader);
                return this;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder addExceptionMapper(ExceptionMapper<? extends Throwable> exceptionMapper) {
            exceptionMappers.add(new ExceptionMapperWrapper<>(exceptionMapper));
            appBuilder.addSingleton(exceptionMapper);
            return this;
        }

        public Builder addExceptionMapper(Class<?> exceptionMapper) {
            try {
                @SuppressWarnings("unchecked") ExceptionMapper<? extends Throwable> em =
                        (ExceptionMapper<? extends Throwable>) exceptionMapper.getConstructor().newInstance();
                exceptionMappers.add(new ExceptionMapperWrapper<>(em));
                appBuilder.addClass(exceptionMapper);
                return this;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
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

        public Builder setParametersValidator(Class<?> parametersValidator) {
            try {
                validator = (ParametersValidator) parametersValidator.getConstructor().newInstance();
                return this;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder putApplicationProperties(String name, Object value) {
            appBuilder.putProperty(name, value);
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
                    Optional.ofNullable(validator),
                    appBuilder.build());
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
