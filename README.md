
# net.cactusthorn.routing

Lightweight Java library for HTTP requests routing in context of Servlet API

The library provides an annotation based API in [JAX-RS specification](https://www.oracle.com/technical-resources/articles/java/jax-rs.html) "like" style to redirect HTTP request to a specific method-class. **And nothing more**. 

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
Then you provide list of the annotated classes to the servlet, plug the servlet in the servlet-container, and... it works.

in combination with embedded Jetty it's looks like that:
```java
import net.cactusthorn.routing.*;
import net.cactusthorn.routing.gson.*;
import net.cactusthorn.routing.thymeleaf.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Application {

    public static void main(String... args) {

        RoutingConfig config =
            RoutingConfig.builder(new MyProvider())
            .addEntryPoint(MyEntryPoint.class)
            .addProducer("application/json", new SimpleGsonProducer())
            .addConsumer("application/json", new SimpleGsonConsumer())
            .addProducer("text/html", new SimpleThymeleafProducer("/thymeleaf/"))
            .build();

        ServletHolder servletHolder = new ServletHolder(new RoutingServlet(config));
        servletHolder.setInitOrder(0);

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");
        servletContext.addServlet(servletHolder, "/*");

        Server jetty = new Server(8080);
        jetty.setHandler(servletContext);

        jetty.start();
        jetty.join();
    }
```
The library is doing only routing, but it's expected that the application provides implementations for several interfaces:
1. ComponentProvider - which will provide EntryPoint instances
1. multiple Producers which will generate responses based on Content-Type
1. multiple Consumers to convert request body to Java objects based on Content-Type

And that is **the basic idea**: you build your web application with only the components you prefer. There is nothing superfluous.
The Routing Library JAR itself is less then 100KB (+ ~ 40KB _SLF4J_ JAR ; + ~ 100KB _javax.servlet-api_ JAR).

The flow is simple:
1. The Servlet get the HTTP request, and find the method which should process it.
1. It request the EntryPoint class instance from the ComponentProvider, convert parameters(if necessary use Consumer to convert request-body) and invoke the method.
1. It get from the method return-Object, find Producer and provide the object to Producer, which write result into response output stream.

### ComponentProvider
It seems that implementation for ComponentProvider is not so easy, because you need Scopes (Request and/or Session) or even Singletons.
But it's not. All of that is natural features of any good dependency injection framework (e.g. [Dagger 2](https://dagger.dev), [Guice](https://github.com/google/guice), [HK2](https://javaee.github.io/hk2/) ). It's anyway good idea to use dependency injection in the application, so all what is necessary in ComponentProvider - link it with dependency injection framework which you are using.

### Producers & Consumers
Providing implementations is relative trivial issue, because there are lot of powerful libraries around, which can do any of that.
As result, implementation of the interface is question of several dozens of code lines. Look at _json-gson_ module as example of _application/json_ Producer & Consumer using [GSON](https://github.com/google/gson) and _thymeleaf_ module as example of _text/html_ Producer using [Thymeleaf](https://www.thymeleaf.org)

### Developing status
It is an early release.

Already:
1. Full functional Servlet
1. @Path for class and/or method (regular expressions support; routing priority like in JAX-RS)
1. @GET @POST and so on
1. Types converting for:
   1. primitive types 
   1. classes with _public static valeuOf(String arg)_ method. 
   1. classes with a public constructor that accepts a single String argument.
   1. classes with _public static fromString(String arg)_ method.
   1. Possibility to write custom convertors.
1. @PathParam, @QueryParam, @FormParam, @CookieParam, @HeaderParam, @FormPart for parameters.
1. Arrays support for @QueryParam & @FormParam
1. Collections support for @QueryParam & @FormParam
   1. Interfaces List\<T\>, Set\<T\>, SortedSet\<T\>, Collection\<T\> where T is supported by type converting
   1. any class which is not abstract and _Collections.class.isAssignableFrom( this class ) == true_
1. @DefaultValue for @PathParam, @QueryParam, @FormParam, @HeaderParam
1. @Produce, @Template and Producer interface. Implementation examples:
   1. application/json: _json-gson_ module
   1. text/html: _thymeleaf_ module
1. @Consumes for class and/or method. To specify Content-Type as additional routing filter. Wildcard is supported (e.g. text/* )
1. Consumer interface support (example: _json-gson_ module)
1. inject HttpServletRequest, HttpServletResponse, HttpSession, ServletContext in method parameters
1. Response class to manually construct response

Comming soon:
1. ComponentProvider example with _dagger 2_

Comming a bit later
1. Java Bean Validation Annotations (JSR 380) for parameters (as additional module)
1. ?




