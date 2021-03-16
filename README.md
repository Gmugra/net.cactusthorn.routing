
# net.cactusthorn.routing

Lightweight [JAX-RS](https://www.oracle.com/technical-resources/articles/java/jax-rs.html) implementation.

[![Build Status](https://travis-ci.com/Gmugra/net.cactusthorn.routing.svg?branch=main)](https://travis-ci.com/Gmugra/net.cactusthorn.routing) [![Coverage Status](https://coveralls.io/repos/github/Gmugra/net.cactusthorn.routing/badge.svg?branch=main)](https://coveralls.io/github/Gmugra/net.cactusthorn.routing?branch=main) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Gmugra/net.cactusthorn.routing.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Gmugra/net.cactusthorn.routing/context:java) [![GitHub release (latest by date)](https://img.shields.io/github/v/release/Gmugra/net.cactusthorn.routing)](https://github.com/Gmugra/net.cactusthorn.routing/releases/tag/v0.29) [![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/net.cactusthorn.routing/core/0.29)](https://search.maven.org/search?q=g:net.cactusthorn.routing) [![GitHub](https://img.shields.io/github/license/Gmugra/net.cactusthorn.routing)](https://github.com/Gmugra/net.cactusthorn.routing/blob/main/LICENSE) [![Build by Maven](http://maven.apache.org/images/logos/maven-feather.png)](http://maven.apache.org)

## Introduction

Small library for HTTP request routing, based on JAX-RS specification.


## Compromises

### MessageBodyReaders & MessageBodyReaders

The library do not provide complex MessageBodyReaders/Writers out of the box.
So, to get support for XML or JSON need to provide MessageBodyReaders/Writers implementations.
However: such implementations are trivial issue.

Examples:
* **json-gson** module as example of _application/json_ Reader & Writer using [GSON](https://github.com/google/gson)
* **thymeleaf** module as example of _text/html_ Writer using [Thymeleaf](https://www.thymeleaf.org)

### ComponentProvider

Implementation of ComponentProvider interface required to  provide JAX-RS resources instances.
It seems that implementation for ComponentProvider is not so easy, because you need "scopes" (Request and/or Session) or even Singletons.
But it's not. All of that is natural features of any good dependency injection framework (e.g. [Dagger 2](https://dagger.dev), [Guice](https://github.com/google/guice), [HK2](https://javaee.github.io/hk2/) ).
It's anyway good idea to use dependency injection in the application, so all what is need for ComponentProvider: link it with dependency injection framework which you are using.

Example:
* **demo-jetty** module is Demo Application: it uses [Dagger 2](https://dagger.dev) for dependency injection and as the basis for the _ComponentProvider_ implementation.

### Usage

Usual JAX-RS resources, e.g:

```java
@Path("/my")
public class MyEntryPoint {

    @GET
    @Path("something/{ id : \\d{6} }/{var1}-{var2}")
    @Produces(MediaType.APPLICATION_JSON)
    public MySomething getIt(
        @PathParam("id") int id,
        @PathParam("var1") String var1,
        @PathParam("var2") String var2,
        @QueryParam("qval") List<Integer> qval) {

        return new MySomething(...);
    }
}

```

Provide list of the annotated classes to the servlet and plug the servlet in the servlet-container.
In combination with embedded Jetty it's looks like that:

```java
import net.cactusthorn.routing.*;
import net.cactusthorn.routing.gson.*;
import net.cactusthorn.routing.thymeleaf.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;

import javax.servlet.MultipartConfigElement;

public class Application {

    public static void main(String... args) {

        ComponentProvider myComponentProvider = new MyComponentProvider(...);
        Collection<Class<?>> resources = ...

        RoutingConfig config =
            RoutingConfig.builder(myComponentProvider)
            .addResource(MyResource.class)
            .addResource(resources)
            .addBodyWriter(new SimpleGsonBodyWriter<>())
            .addBodyReader(new SimpleGsonBodyReader<>())
            .addBodyWriter(new SimpleThymeleafBodyWriter("/thymeleaf/"))
            .addParamConverterProvider(new LocalDateParamConverterProvider())
            .addExceptionMapper(new UnsupportedOperationExceptionMapper())
            .setParametersValidator(new SimpleParametersValidator())
            .build();

        MultipartConfigElement mpConfig = new MultipartConfigElement("/tmp", 1024 * 1024, 1024 * 1024 * 5, 1024 * 1024 * 5 * 5);

        ServletHolder servletHolder = new ServletHolder(new RoutingServlet(config));
        servletHolder.setInitOrder(0);
        servletHolder.getRegistration().setMultipartConfig(mpConfig);

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");
        servletContext.addServlet(servletHolder, "/*");

        Server jetty = new Server(8080);
        jetty.setHandler(servletContext);
        jetty.start();
        jetty.join();
    }
}

```

## Supported features

### Request to resource matching

1. URI template matched to request URI according JAX-RS rules
1. If no one resource found then HTTP response with status 404 send back to client.
1. If some resources was found, but `@Consumes`-annotation of no one of them fit Content-Type request-header: HTTP response with status 415 send back to client.
1. If some resources was found, but `@Produces`-annotation of no one of them fit Accept request-header: HTTP response with status 406 send back to client.

### Path matching precedence rules
The JAX-RS specification has defined strict sorting and precedence rules for matching URI expressions and is based on a most specific match wins algorithm.
The JAX-RS provider gathers up the set of deployed URI expressions and sorts them based on the following logic:
1. The primary key of the sort is the number of literal characters in the full URI matching pattern. The sort is in descending order.
1. The secondary key of the sort is the number of template expressions embedded within the pattern (e.g. {id} or {id : .+}). This sort is in descending order.
1. The tertiary key of the sort is the number of nondefault template expressions. A default template expression is one that does not define a regular expression (e.g. {id}).

### Method parameter types converting
The type of the annotated parameter must either:
1. Be a primitive type
1. Have a constructor that accepts a single String argument
1. Have a static method named `valueOf` or `fromString` that accepts a single String argument (see, for example, `Integer.valueOf(String)`)
   1. If both methods are present then `valueOf` used unless the type is an enum in which case `fromString` used.
1. Have a registered implementation of `javax.ws.rs.ext.ParamConverterProvider` JAX-RS extension SPI that returns a `ParamConverter` instance capable of a "from string" conversion for the type.
1. Be List\<T\>, Set\<T\> or SortedSet\<T\>, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.

### Supported JAX-RS features

1. `@Path` for class and/or method
   * path-parameters (with regular expressions support)
1. `@GET` `@POST` `@DELETE` `@HEAD` `@OPTIONS` `@PATCH` `@PUT` for method
1. `@PathParam`, `@QueryParam`, `@FormParam`, `@CookieParam`, `@HeaderParam`, `@FormPart` for method parameters.
1. `@DefaultValue`
1. `@Consumes` for class and/or method
   * default(if not present) is "\*/\*"
1. `@Produces` for class and/or method
   * default(if not present) is "text/plain"
1. `@Context` for method parameters. At the moment suport next types:
   * javax.servlet.http.HttpServletRequest
   * javax.servlet.http.HttpServletResponse
   * javax.servlet.ServletContext
   * javax.ws.rs.core.SecurityContext
   * javax.ws.rs.core.HttpHeaders
   * javax.ws.rs.core.UriInfo
1. `javax.ws.rs.core.Response`
1. `javax.ws.rs.ext.ExceptionMapper`
1. `javax.ws.rs.ext.ParamConverterProvider`
1. `javax.ws.rs.core.UriBuilder`
1. `javax.ws.rs.core.Link.Builder`
1. `javax.ws.rs.core.Variant.VariantListBuilder`
1. `javax.ws.rs.core.Form`

### Extensions

1. ParametersValidator interface to integrate additional validations e.g. _javax.validation_
   * Implemetation example is **validation-javax** module
1. `javax.annotation.security.RolesAllowed` method annotation
   1. to check entry point against request.isUserInRole(...)
   1. Implemetation example exists in **demo-jetty** module
1. `@Template` annotation, `Templated`-class and `TemplatedMessageBodyWriter` to implement message body writers for html-template-engines (e.g. [FreeMarker](https://freemarker.apache.org), [Thymeleaf](https://www.thymeleaf.org) )
   * Implemetation example is **thymeleaf** module
1. Default parameter name
   1. Only when project compiled with `javac -parameters`
   1. Parameters annotation can be used with empty-string as name, e.g: `@QueryParam("") List<Integer> qval`. In this case paramener name will be used to match parameter in request.


### ParamConverterProvider

Default(if the annotation is not present) priority is `javax.ws.rs.Priorities.USER`

Example:
```java
@javax.annotation.Priority(3000)
public class LocalDateParamConverterProvider implements javax.ws.rs.ext.ParamConverterProvider {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    private static final javax.ws.rs.ext.ParamConverter<LocalDate> CONVERTER = new javax.ws.rs.ext.ParamConverter<LocalDate>() {

        @Override
        public LocalDate fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(value, FORMATTER);
        }

        @Override
        public String toString(LocalDate value) {
            if (value == null) {
                return null;
            }
            return value.format(FORMATTER);
        }
    };

    @Override @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDate.class) {
            return (ParamConverter<T>) CONVERTER;
        }
        return null;
    }
}
```

### MessageBodyReaders

Default(if the annotation is not present) priority is `javax.ws.rs.Priorities.USER`

Default(if the annotation is not present) consumes is "\*/*\"

For the next types MessageBodyReaders are provided:
Type | Priority
-----| --------
java.io.InputStream | 50
java.lang.String | 50
Any convertable from String | 9999

#### Simple

```java
@javax.annotation.Priority(3000)
@javax.ws.rs.Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class MyClassMessageBodyReader implements javax.ws.rs.ext.MessageBodyReader<MyClass> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ...
    }

    @Override
    public MyClass readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        ...
    }
}
```

#### Initializable

```java
@javax.annotation.Priority(3000)
@javax.ws.rs.Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class MyClassMessageBodyReader implements net.cactusthorn.routing.body.reader.InitializableMessageBodyReader<MyClass> {

    @Override
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        ...
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ...
    }

    @Override
    public MyClass readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        ...
    }
}
```

### MessageBodyWriters

Default(if the annotation is not present) priority is `javax.ws.rs.Priorities.USER`

Default(if the annotation is not present) produces is "\*/*\"

For the next types MessageBodyReaders are provided:
Type | Priority
-----| --------
java.lang.String | 50
java.lang.Object | 9999


#### Simple
```java
@javax.annotation.Priority(3000)
@javax.ws.rs.Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class MyClassMessageBodyWriter implements javax.ws.rs.ext.MessageBodyWriter<MyClass> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ...
    }

    @Override
    public void writeTo(MyClass entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        ...
    }
}
```

#### Initializable
```java
@javax.annotation.Priority(3000)
@javax.ws.rs.Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_HTML})
public class MyClassMessageBodyWriter implements net.cactusthorn.routing.body.writer.InitializableMessageBodyWriter<MyClass> {

    @Override
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        ...
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ...
    }

    @Override
    public void writeTo(MyClass entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        ...
    }
}
```

#### Templated
```java
@Produces({MediaType.TEXT_HTML})
public class SimpleThymeleafBodyWriter implements net.cactusthorn.routing.body.writer.TemplatedMessageBodyWriter {

    @Override
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        ...
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        ...
    }

    @Override
    public void writeTo(Templated templated, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        ...
    }
}
```

### ExceptionMappers

Default(if the annotation is not present) priority is `javax.ws.rs.Priorities.USER`

#### Example
```java
@javax.annotation.Priority(3000)
public static class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {

    @Override
    public Response toResponse(UnsupportedOperationException exception) {
        return Response.status(Response.Status.CONFLICT).build();
    }
}
```

## DOWNLOAD

[Maven Central Repository](https://search.maven.org/search?q=g:net.cactusthorn.routing):
```xml
<dependency>
    <groupId>net.cactusthorn.routing</groupId>
    <artifactId>core</artifactId>
    <version>0.29</version>
</dependency>
```

Public Releases can be also downloaded from [GitHub Releases](https://github.com/Gmugra/net.cactusthorn.routing/releases) or [GitHub Packages](https://github.com/Gmugra?tab=packages&repo_name=net.cactusthorn.routing)

## LICENSE
net.cactusthorn.routing is released under the BSD 3-Clause license. See LICENSE file included for the details.
