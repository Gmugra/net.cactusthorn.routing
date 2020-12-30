
# net.cactusthorn.routing

Lightweight Java library for HTTP requests routing in context of Servlet API

[![Build Status](https://travis-ci.com/Gmugra/net.cactusthorn.routing.svg?branch=main)](https://travis-ci.com/Gmugra/net.cactusthorn.routing) [![Coverage Status](https://coveralls.io/repos/github/Gmugra/net.cactusthorn.routing/badge.svg?branch=main)](https://coveralls.io/github/Gmugra/net.cactusthorn.routing?branch=main) ![GitHub release (latest by date)](https://img.shields.io/github/v/release/Gmugra/net.cactusthorn.routing) [![Build by Maven](http://maven.apache.org/images/logos/maven-feather.png)](http://maven.apache.org)

## Introduction

The library provides an annotation based API in [JAX-RS specification](https://www.oracle.com/technical-resources/articles/java/jax-rs.html) "like" style to redirect HTTP request to a specific method. **And nothing more**.

Usual annotations in usual way e.g.:
```java
import net.cactusthorn.routing.annotation.*;

@Path("/my")
public class MyEntryPoint {

    @GET
    @Path("something/{ id : \\d{6} }/{var1}-{var2}")
    @Produces("application/json")
    public MySomething getIt(
        @PathParam("id") int id,
        @PathParam("var1") String var1,
        @PathParam("var2") String var2,
        @QueryParam("qval") List<Integer> qval) {

        return new MySomething(...);
    }
}
```
Provide list of the annotated classes to the servlet, plug the servlet in the servlet-container, and... it works.

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

        ComponentProvider provider = new MyComponentProvider(...);
        Collection<Class<?>> entryPoints = ...
        Converter converter = new MyLocalDateConverter(...);

        RoutingConfig config =
            RoutingConfig.builder(provider)
            .addEntryPoint(entryPoints)
            .addProducer("application/json", new SimpleGsonProducer())
            .addConsumer("application/json", new SimpleGsonConsumer())
            .addProducer("text/html", new SimpleThymeleafProducer("/thymeleaf/"))
            .addConverter(java.time.LocalDate.class, converter);
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
The library is doing only routing, but it's expected that the application provides implementations for several interfaces:
1. ComponentProvider - which will provide EntryPoint instances
1. multiple Producers which will generate responses based on Content-Type
1. multiple Consumers to convert request body to Java objects based on Content-Type

**The basic idea**: build web application with only the components you prefer.
The Routing Library JAR itself is less then 100KB (+ ~ 40KB _SLF4J_ JAR ; + ~ 100KB _javax.servlet-api_ JAR).

The flow is simple:
1. The Servlet get the HTTP request, and find the method which should process it.
1. It get the EntryPoint instance from the ComponentProvider, convert parameters(if necessary use Consumer to convert request-body) and invoke the method.
1. It get from the method return-Object, find Producer and provide the object to Producer, which write result into response output stream.

#### ComponentProvider
It seems that implementation for ComponentProvider is not so easy, because you need "scopes" (Request and/or Session) or even Singletons.
But it's not. All of that is natural features of any good dependency injection framework (e.g. [Dagger 2](https://dagger.dev), [Guice](https://github.com/google/guice), [HK2](https://javaee.github.io/hk2/) ). It's anyway good idea to use dependency injection in the application, so all what is necessary in ComponentProvider - link it with dependency injection framework which you are using.

#### Producers & Consumers
Providing implementations is relative trivial issue, because there are lot of powerful libraries around, which can do any of that.
As result, implementation of the interface is question of several lines of code. Look at **json-gson** module as example of _application/json_ Producer & Consumer using [GSON](https://github.com/google/gson) and **thymeleaf** module as example of _text/html_ Producer using [Thymeleaf](https://www.thymeleaf.org)

## Example

The **demo-jetty** module is full functional example.

It uses the embedded [Jetty](https://www.eclipse.org/jetty/) as servlet-container,
and [Dagger 2](https://dagger.dev) for dependency injection and as the basis for the _ComponentProvider_.

More or less there are examples of everything:
Various "simple" requests, JSON, File uploading (multipart/form-data), HTML with Thymeleaf, Scopes, parameters validation with javax.validation

##  Features

1. @Path for class and/or method (regular expressions support; routing priority like in JAX-RS)
1. @GET @POST and so on
1. Types converting for:
   1. primitive types
   1. classes with _public static valeuOf(String arg)_ method.
   1. classes with a public constructor that accepts a single String argument.
   1. classes with _public static fromString(String arg)_ method.
   1. _Converter_ interface: to write custom converters.
1. @PathParam, @QueryParam, @FormParam, @CookieParam, @HeaderParam, @FormPart for parameters.
1. Arrays support for @QueryParam & @FormParam
1. Collections support for @QueryParam & @FormParam
   1. Interfaces List\<T\>, Set\<T\>, SortedSet\<T\>, Collection\<T\> where T is supported by type converting
   1. any class which is not abstract and _Collections.class.isAssignableFrom( this class ) == true_
1. @DefaultValue for @PathParam, @QueryParam, @FormParam, @HeaderParam
1. @Produce, @Template and Producer interface. Implementation examples:
   1. application/json: _SimpleGsonProducer_ (**json-gson** module)
   1. text/html: _SimpleThymeleafProducer_ (**thymeleaf** module)
1. @Consumes for class and/or method. To specify Content-Type as additional routing filter. Wildcard is supported (e.g. text/* )
1. Consumer interface. Implementation examples:
   1. application/json: _SimpleGsonConsumer_ (**json-gson** module)
1. inject HttpServletRequest, HttpServletResponse, HttpSession, ServletContext in method parameters
1. _Response_ class to manually construct response.
1. ParametersValidator interface to integrate additional validations e.g. _javax.validation_
   1. Implemetation example is **validation-javax** module

##  LICENSE

net.cactusthorn.routing is released under the BSD license. See LICENSE file included for the details.



