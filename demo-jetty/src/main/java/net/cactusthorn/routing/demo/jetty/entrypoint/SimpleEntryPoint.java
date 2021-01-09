package net.cactusthorn.routing.demo.jetty.entrypoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import net.cactusthorn.routing.Response;
import net.cactusthorn.routing.annotation.Produces;
import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.annotation.UserRoles;
import net.cactusthorn.routing.demo.jetty.dagger.EntryPoint;

public class SimpleEntryPoint implements EntryPoint {

    @Inject //
    public SimpleEntryPoint() {
    }

    @GET @Produces("text/html") @Template("/index.html") //
    public void doroot() {
    }

    @GET @Path("/nocontent") //
    public void nocontent() {
    }

    @GET @Path("/rest/api/test{ var : \\d+ }") //
    public String doit(@PathParam("var") int in, @DefaultValue("10.5") @QueryParam("") Double test) {
        return in + " \u00DF " + test + " :: " + this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    @GET @Path("/rest/api/validation") //
    public String validation(@NotNull @Min(5) @QueryParam("test") String s) {
        return ">>>" + s;
    }

    @GET @Path("/rest/api/{var : [abc]*}") //
    public String empty(@PathParam("var") @DefaultValue("DEFAULT") String sss) {
        return "|" + sss + "| :: " + this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    @GET @Path("/seeother") //
    public Response seeother() throws URISyntaxException {
        return Response.builder().seeOther(new URI("/rest/api/test30?test=33.45")).build();
    }

    @GET @Path("/localdate/{date : \\d{8}}") //
    public String localdate(@PathParam("date") LocalDate date) {
        return date.toString();
    }

    @GET @UserRoles({ "TestRole" }) @Path("/principal") //
    public String principal(Principal principal) {
        return principal.getName();
    }

    @GET @UserRoles({ "WrongRole" }) @Path("/wrongrole") //
    public void wrongRole() {
    }
}
