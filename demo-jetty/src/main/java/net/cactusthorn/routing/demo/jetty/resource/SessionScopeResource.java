package net.cactusthorn.routing.demo.jetty.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import net.cactusthorn.routing.demo.jetty.dagger.*;
import net.cactusthorn.routing.demo.jetty.service.IAmSingletonService;

@SessionScope //
public class SessionScopeResource implements Resource {

    private int counter = 0;

    private IAmSingletonService iam;

    @Inject //
    public SessionScopeResource(IAmSingletonService iam) {
        this.iam = iam;
    }

    @GET @Path("count") //
    public String count() {
        counter++;
        return this.getClass().getSimpleName() + "@" + this.hashCode() + " :: " + counter + " :: " + iam.iam();
    }
}
