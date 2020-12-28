package net.cactusthorn.routing.demo.jetty.entrypoint;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import net.cactusthorn.routing.Response;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.demo.jetty.dagger.EntryPoint;

public class SimpleEntryPoint implements EntryPoint {

    @Inject //
    public SimpleEntryPoint() {
    }

    @GET //
    public String doroot() {
        return "ROOT :: " + this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    @GET @Path("/nocontent") //
    public void nocontent() {
    }

    @GET @Path("/rest/api/test{ var : \\d+ }") //
    public String doit(@PathParam("var") int in, @DefaultValue("10.5") @QueryParam("test") Double q) {
        return in + " \u00DF " + q + " :: " + this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    @GET @Path("/rest/api/{var : [abc]*}") //
    public String empty(@PathParam("var") @DefaultValue("DEFAULT") String sss) {
        return "|" + sss + "| :: " + this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    @GET @Path("/seeother") //
    public Response seeother() throws URISyntaxException {
        return Response.builder().seeOther(new URI("/rest/api/test30?test=33.45")).build();
    }
}
