
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
    public MySomething getIt(@PathParam("id") int id, @PathParam("var1") String var1, @PathParam("var1") String var2) {
        return new MySomething(...);
    }
}
```
Then you provide list of the annotated classes to the servlet, plug the servlet in the servlet-container, and... it works.

in combination with embedded Jetty it's looks like that:
```java
import net.cactusthorn.routing.*;
import net.cactusthorn.routing.gson.SimpleGsonProducer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Application {

    public static void main(String... args) {

        RoutingConfig config = RoutingConfig.builder(new Provider()).addEntryPoint(MyEntryPoint.class)
                .addProducer("application/json", new SimpleGsonProducer(true)).build();

        ServletHolder servletHolder = new ServletHolder(new RoutingServlet(config));
        servletHolder.setInitOrder(0);

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");
        servletContext.addServlet(servletHolder, "/rest/*");

        Server jetty = new Server(8080);
        jetty.setHandler(servletContext);

        jetty.start();
        jetty.join();
    }
```
The library is doing only routing, but it's expected that the application will provide implementations for several interfaces:
1. CompoentProvider - which will provide EntryPoint instances
1. multiple Producers: which will generate responses based on Content-Type
1. multiple Consumers to convert request body to Java objects based on Content-Type

The flow is simple:
1. The Servlet get the HTTP request, and find the method which should process it.
1. It request the EntryPoint class instance from the CompoentProvider, convert parameters and invoke the method.
1. It get from the method return-Object, find Producer and provide the object to Producer, which write result into response output stream.


Providing of such implementations is relative trivial issue, because there are lot of powerful libraries around, which can do any of that.
And implementation of the interface is question of several dozens of code lines. (look at _json-gson_ module as example of JSON Producer with [GSON](https://github.com/google/gson))

And that **is the basic idea**: you build your web application with only the components you prefer. There is nothing superfluous.

### ComponentProvider
It seems that implementation for ComponentProvider is not so easy, because you need Scopes (Request and/or Session) or even Singletons.
But it's not. All of that is natural features of any good dependency injection framework (e.g. [Dagger 2](https://dagger.dev), [Guice](https://github.com/google/guice), [HK2](https://javaee.github.io/hk2/) )
It's anyway very good idea to use dependency injection in your application, so all you need in ComponentProvider - link it to dependency injection framework which you are using: several dozens of code lines.

### Developing status
It is an early release, not everything is implemented.

Already:
1. Full functional Servlet
1. @Path for class and/or method (regular expressions support; routing priority like in JAX-RS)
1. @GET @POST and so on
1. Types converting for primitive types and classes with _public static valeuOf(String arg)_ method. And possibility to write custom convertors.
1. @Produce and Producers (example: _json-gson_ module)
1. @Consumes for class and/or method. To specify Content-Type as additional routing filter. Wildcard is supported (e.g. text/* )
1. @Context to get next instances in the EntryPoint-method: HttpServletRequest, HttpServletResponse, HttpSession, ServletContext
1. @PathParam and @QueryParam for parameters.

Comming soon:
1. Type converting for classes with _public static fromString(String arg)_ method.
1. Type converting for classes with a constructor that accepts a single String argument.
1. Consumer interface support (and example with Gson)
1. Producer example for text/html with [Thymeleaf](https://www.thymeleaf.org)
1. ComponentProvider example with _dagger 2_

Comming a bit later
1. @HeaderParam for parameters
1. @CookieParam for parameters
1. @DefaultValue for parameters
1. Java Bean Validation Annotations (JSR 380) for parameters (as additional module)
1. ?




