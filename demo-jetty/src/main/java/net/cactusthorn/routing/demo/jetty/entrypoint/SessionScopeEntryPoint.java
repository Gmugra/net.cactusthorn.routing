package net.cactusthorn.routing.demo.jetty.entrypoint;

import javax.inject.Inject;

import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.demo.jetty.dagger.*;
import net.cactusthorn.routing.demo.jetty.service.IAmSingletonService;

@SessionScope //
public class SessionScopeEntryPoint implements EntryPoint {

    private int counter = 0;

    private IAmSingletonService iam;

    @Inject //
    public SessionScopeEntryPoint(IAmSingletonService iam) {
        this.iam = iam;
    }

    @GET @Path("count") //
    public String count() {
        counter++;
        return this.getClass().getSimpleName() + "@" + this.hashCode() + " :: " + counter + " :: " + iam.iam();
    }
}
