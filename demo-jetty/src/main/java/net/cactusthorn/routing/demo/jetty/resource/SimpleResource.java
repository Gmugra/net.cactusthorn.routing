package net.cactusthorn.routing.demo.jetty.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.demo.jetty.dagger.Resource;

public class SimpleResource implements Resource {

    @Inject //
    public SimpleResource() {
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
        return Response.status(Status.SEE_OTHER).location(new URI("/rest/api/test30?test=33.45")).build();
    }

    @GET @Path("/localdate/{date : \\d{8}}") //
    public String localdate(@PathParam("date") LocalDate date) {
        return date.toString();
    }

    @GET @RolesAllowed({ "TestRole" }) @Path("/principal") //
    public String principal(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }

    @GET @RolesAllowed({ "WrongRole" }) @Path("/wrongrole") //
    public void wrongRole() {
    }

    @GET @Path("/headers") //
    public String headers(@Context HttpHeaders headers) {
        return headers.getHeaderString(HttpHeaders.USER_AGENT);
    }
}
